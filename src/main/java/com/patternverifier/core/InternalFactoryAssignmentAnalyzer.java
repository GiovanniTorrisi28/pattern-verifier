package com.patternverifier.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Distingue un metodo "factory interno" che alimenta lo stato della classe (Strategy/State
 * es. {@code SelectionTool.mouseDown()}: {@code fChild = createHandleTracker(...)}) da
 * un metodo che restituisce lo stesso tipo ma lo consegna a un chiamante esterno senza mai
 * conservarlo (Factory Method puro, es. {@code AbstractFigure.connectorAt()}:
 * {@code return new ChopBoxConnector(this)}, mai assegnato a un campo).
 *
 * <p>Cerca in ogni metodo una chiamata a un proprio metodo (INVOKEVIRTUAL o INVOKESPECIAL con
 * owner uguale alla classe stessa) il cui risultato finisca in un'assegnazione a un proprio campo
 * (PUTFIELD) di tipo assegnabile al target. Riconosce due forme: quella diretta
 * ({@code campo = metodoProprio(...);}, in cui l'invocazione è immediatamente seguita dal PUTFIELD)
 * e quella con indirection tramite variabile locale ({@code X v = metodoProprio(...); campo = v;}),
 * tracciando quali locali contengono il risultato dell'invocazione lungo il codice sequenziale.
 *
 * <p><b>Sicurezza contro i falsi positivi.</b> Il tracciamento delle locali viene disabilitato al
 * primo salto o switch incontrato nel metodo: come per {@link SelfReturnAnalyzer}, questo
 * garantisce che si possa solo mancare un'assegnazione realmente presente (falso negativo), mai
 * affermarne una assente. Non si azzera invece in corrispondenza delle label, che javac emette
 * anche per la tabella dei numeri di riga e che non sono confini di flusso di controllo. Non è
 * riconosciuta l'indirection che attraversa un ramo o un metodo intermedio; la forma diretta
 * (invocazione immediatamente seguita da {@code PUTFIELD}) resta riconosciuta anche dopo un ramo.
 */
public class InternalFactoryAssignmentAnalyzer extends ClassVisitor {

    private String selfInternalName;
    private final String targetTypeName;
    private boolean found;

    private InternalFactoryAssignmentAnalyzer(String targetTypeName) {
        super(Opcodes.ASM9);
        this.targetTypeName = targetTypeName;
    }

    @Override
    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces) {
        this.selfInternalName = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                     String signature, String[] exceptions) {
        return new AssignmentScanner();
    }

    private class AssignmentScanner extends MethodVisitor {
        // Vero quando il valore in cima allo stack è il risultato di una chiamata a un proprio
        // metodo (direttamente, o ricaricato da una locale che lo conteneva).
        private boolean pendingSelfInvoke = false;
        // Locali che, nel tratto sequenziale corrente, contengono il risultato di una chiamata a
        // un proprio metodo.
        private final Set<Integer> localsHoldingResult = new HashSet<>();
        // Disabilitato al primo salto/switch: garanzia contro i falsi positivi, come in
        // SelfReturnAnalyzer. Non si azzera sulle label (javac ne emette per i numeri di riga).
        private boolean localTrackingDisabled = false;

        AssignmentScanner() { super(Opcodes.ASM9); }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name,
                                    String descriptor, boolean isInterface) {
            pendingSelfInvoke = (opcode == Opcodes.INVOKEVIRTUAL || opcode == Opcodes.INVOKESPECIAL)
                    && owner.equals(selfInternalName);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            if (pendingSelfInvoke && opcode == Opcodes.PUTFIELD && owner.equals(selfInternalName)) {
                String fieldType = Type.getType(descriptor).getClassName();
                if (TypeHierarchy.isAssignable(fieldType, targetTypeName)) {
                    found = true;
                }
            }
            pendingSelfInvoke = false;
        }

        @Override
        public void visitVarInsn(int opcode, int var) {
            if (opcode == Opcodes.ASTORE) {
                // La locale eredita lo stato dello stack: contiene il risultato dell'invocazione
                // solo se lo conteneva davvero e il tracciamento è ancora attivo.
                if (pendingSelfInvoke && !localTrackingDisabled) {
                    localsHoldingResult.add(var);
                } else {
                    localsHoldingResult.remove(var);
                }
                pendingSelfInvoke = false;
            } else if (opcode == Opcodes.ALOAD) {
                // Ricarico una locale: il risultato torna in cima allo stack solo se la locale lo
                // conteneva. ALOAD di this (var 0) o di altre locali non tracciate azzera il flag.
                pendingSelfInvoke = localsHoldingResult.contains(var);
            } else {
                if (isStore(opcode)) {
                    localsHoldingResult.remove(var);
                }
                pendingSelfInvoke = false;
            }
        }

        private boolean isStore(int opcode) {
            return opcode == Opcodes.ISTORE || opcode == Opcodes.LSTORE
                    || opcode == Opcodes.FSTORE || opcode == Opcodes.DSTORE;
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            // Un CHECKCAST sul risultato (es. campo = (T) metodoProprio()) non lo consuma.
            if (opcode != Opcodes.CHECKCAST) {
                pendingSelfInvoke = false;
            }
        }

        // Primo confine di controllo: disabilita il tracciamento delle locali per il resto del
        // metodo. La forma diretta (INVOKE immediatamente seguito da PUTFIELD) resta riconosciuta
        // anche dopo un ramo, perché non dipende dalle locali.
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
            pendingSelfInvoke = false;
            localTrackingDisabled = true;
            localsHoldingResult.clear();
        }

        @Override public void visitInsn(int opcode) { pendingSelfInvoke = false; }
        @Override public void visitIntInsn(int opcode, int operand) { pendingSelfInvoke = false; }
        @Override public void visitLdcInsn(Object value) { pendingSelfInvoke = false; }
        @Override public void visitIincInsn(int var, int increment) { pendingSelfInvoke = false; }

        @Override
        public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
            pendingSelfInvoke = false;
        }

        @Override
        public void visitInvokeDynamicInsn(String name, String descriptor,
                                           Handle bootstrapMethodHandle,
                                           Object... bootstrapMethodArguments) {
            pendingSelfInvoke = false;
        }
    }

    /**
     * @param className      classe da analizzare — solo il proprio bytecode, non quello delle
     *                       superclassi: un metodo ereditato non compare nel file .class della
     *                       sottoclasse, quindi lo scan è già naturalmente limitato ai metodi
     *                       dichiarati dalla classe stessa.
     * @param targetTypeName tipo (Strategy o State) di cui cercare l'assegnazione a campo
     */
    public static boolean storesInvocationResultInField(String className, String targetTypeName) {
        String resourcePath = className.replace('.', '/') + ".class";
        try (InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Bytecode non trovato per: " + className);
            }
            ClassReader reader = new ClassReader(is);
            InternalFactoryAssignmentAnalyzer analyzer = new InternalFactoryAssignmentAnalyzer(targetTypeName);
            reader.accept(analyzer, ClassReader.SKIP_FRAMES);
            return analyzer.found;
        } catch (IOException e) {
            throw new RuntimeException("Errore nella lettura del bytecode di " + className, e);
        }
    }
}
