package com.patternverifier.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Individua i metodi che restituiscono realmente {@code this}, distinguendo una fluent interface
 * da un metodo che si limita a dichiarare il tipo del Builder come tipo di ritorno pur
 * restituendo un'altra istanza (es. un builder immutabile che ne costruisce una copia).
 *
 * <p>Il tipo di ritorno dichiarato non basta: {@code Builder withName(String)} è soddisfatto sia
 * da {@code return this;} sia da {@code return new Builder(...);}, due comportamenti diversi che
 * la sola firma non separa. La distinzione richiede di leggere le istruzioni del corpo.
 *
 * <p>Riconosce la sequenza bytecode generata da javac per {@code return this;} — {@code ALOAD_0}
 * seguito da {@code ARETURN} — tollerando un {@code CHECKCAST} intermedio, forma prodotta dal
 * cast {@code (T) this} nei builder generici auto-tipizzati. Riconosce inoltre l'indirection
 * tramite variabile locale ({@code T self = this; ... return self;}) tracciando quali locali
 * contengono {@code this} lungo il codice sequenziale.
 *
 * <p><b>Sicurezza contro i falsi positivi.</b> Il tracciamento delle locali viene disabilitato al
 * primo salto o switch incontrato nel metodo, e non riattivato. Senza questa cautela, una locale
 * che contiene {@code this} su un ramo e un altro valore su un ramo diverso potrebbe far
 * riconoscere per errore un {@code return locale} come {@code return this}. Si garantisce così che
 * l'analizzatore possa solo mancare una relazione realmente presente (falso negativo), mai
 * affermarne una assente (falso positivo): la sequenza riconosciuta implica sempre un {@code this}
 * effettivamente restituito. Il riconoscimento diretto ({@code ALOAD_0} seguito da
 * {@code ARETURN}) resta attivo anche dopo un ramo, perché non dipende dalle locali.
 *
 * <p>Si noti che <b>non</b> si azzera in corrispondenza delle label: javac ne emette anche per la
 * tabella dei numeri di riga, che non sono confini di flusso di controllo, e azzerare lì
 * spegnerebbe il tracciamento tra una riga e la successiva, rendendo inefficace il riconoscimento
 * dell'indirection.
 *
 * <p><b>Limiti dichiarati.</b> I metodi {@code static} sono esclusi: in un metodo statico lo slot 0
 * non è {@code this} ma il primo parametro. Non è riconosciuta l'indirection che attraversa un
 * confine di controllo (per la cautela sopra) né quella tramite un metodo intermedio
 * ({@code return self();}). Vengono analizzati soltanto i metodi dichiarati dalla classe stessa:
 * un metodo fluente ereditato da un builder base astratto non compare nel file {@code .class}
 * della sottoclasse e non viene quindi rilevato.
 */
public class SelfReturnAnalyzer extends ClassVisitor {

    private final Set<String> selfReturningMethods = new HashSet<>();

    private SelfReturnAnalyzer() {
        super(Opcodes.ASM9);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                     String signature, String[] exceptions) {
        // In un metodo statico lo slot 0 è il primo parametro, non this: analizzarlo
        // produrrebbe falsi positivi.
        if ((access & Opcodes.ACC_STATIC) != 0) {
            return null;
        }
        return new SelfReturnScanner(name);
    }

    private class SelfReturnScanner extends MethodVisitor {
        private final String methodName;
        private boolean thisOnStack = false;
        // Variabili locali che, nel tratto di codice sequenziale corrente, contengono this.
        // Consente di riconoscere anche la forma "T self = this; ... return self;", in cui il
        // valore passa per una variabile intermedia.
        private final Set<Integer> localsHoldingThis = new HashSet<>();
        // Il tracciamento delle locali viene disabilitato al primo salto/switch incontrato nel
        // metodo. È la garanzia di sicurezza contro i falsi positivi: in presenza di flusso di
        // controllo, una locale potrebbe contenere this su un ramo e un altro valore su un altro,
        // e un'analisi che ignora i rami potrebbe riconoscere erroneamente un "return locale" come
        // "return this". Disattivando il tracciamento dopo il primo ramo si esclude il caso, al
        // prezzo di qualche falso negativo, mai di un falso positivo. NB: NON si azzera sulle
        // label, perché javac ne emette anche per la tabella dei numeri di riga, che non sono
        // confini di controllo. Il riconoscimento diretto (ALOAD_0 seguito da ARETURN) resta
        // attivo anche dopo un ramo, perché non dipende dalle locali.
        private boolean localTrackingDisabled = false;

        SelfReturnScanner(String methodName) {
            super(Opcodes.ASM9);
            this.methodName = methodName;
        }

        @Override
        public void visitVarInsn(int opcode, int var) {
            if (opcode == Opcodes.ALOAD) {
                thisOnStack = (var == 0) || localsHoldingThis.contains(var);
            } else if (opcode == Opcodes.ASTORE) {
                // La locale eredita lo stato dello stack: diventa "this" solo se lo era davvero e
                // il tracciamento è ancora attivo; altrimenti smette di esserlo.
                if (thisOnStack && !localTrackingDisabled) {
                    localsHoldingThis.add(var);
                } else {
                    localsHoldingThis.remove(var);
                }
                thisOnStack = false;
            } else {
                // Store di un valore non-riferimento (ISTORE/LSTORE/...) sullo stesso slot: la
                // locale non contiene più this. I load non-ALOAD non lasciano this sullo stack.
                if (isStore(opcode)) {
                    localsHoldingThis.remove(var);
                }
                thisOnStack = false;
            }
        }

        private boolean isStore(int opcode) {
            return opcode == Opcodes.ISTORE || opcode == Opcodes.LSTORE
                    || opcode == Opcodes.FSTORE || opcode == Opcodes.DSTORE;
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            // Un CHECKCAST non consuma il riferimento a this: è la forma generata dal cast
            // (T) this nei builder generici. Ogni altra istruzione di tipo lo invalida.
            if (opcode != Opcodes.CHECKCAST) {
                thisOnStack = false;
            }
        }

        @Override
        public void visitInsn(int opcode) {
            if (opcode == Opcodes.ARETURN && thisOnStack) {
                selfReturningMethods.add(methodName);
            }
            thisOnStack = false;
        }

        // Primo confine di controllo: disabilita il tracciamento delle locali per il resto del
        // metodo (vedi commento su localTrackingDisabled).
        @Override public void visitJumpInsn(int opcode, Label label) { onBranch(); }

        @Override
        public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
            onBranch();
        }

        @Override
        public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
            onBranch();
        }

        private void onBranch() {
            thisOnStack = false;
            localTrackingDisabled = true;
            localsHoldingThis.clear();
        }

        @Override public void visitIntInsn(int opcode, int operand) { thisOnStack = false; }
        @Override public void visitFieldInsn(int o, String w, String n, String d) { thisOnStack = false; }
        @Override public void visitLdcInsn(Object value) { thisOnStack = false; }
        @Override public void visitIincInsn(int var, int increment) { thisOnStack = false; }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name,
                                    String descriptor, boolean isInterface) {
            thisOnStack = false;
        }

        @Override
        public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
            thisOnStack = false;
        }

        @Override
        public void visitInvokeDynamicInsn(String name, String descriptor,
                                           Handle bootstrapMethodHandle,
                                           Object... bootstrapMethodArguments) {
            thisOnStack = false;
        }
    }

    /**
     * @param className classe da analizzare
     * @return i nomi dei metodi dichiarati dalla classe il cui corpo restituisce {@code this}
     */
    public static Set<String> findSelfReturningMethods(String className) {
        String resourcePath = className.replace('.', '/') + ".class";
        try (InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Bytecode non trovato per: " + className);
            }
            ClassReader reader = new ClassReader(is);
            SelfReturnAnalyzer analyzer = new SelfReturnAnalyzer();
            reader.accept(analyzer, ClassReader.SKIP_FRAMES);
            return analyzer.selfReturningMethods;
        } catch (IOException e) {
            throw new RuntimeException("Errore nella lettura del bytecode di " + className, e);
        }
    }
}
