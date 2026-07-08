package com.patternverifier.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Verifica se una classe invoca metodi su istanze di un tipo target,
 * scansionando TUTTI i metodi della classe (non solo uno specifico).
 *
 * Differenza rispetto a TemplateMethodBodyAnalyzer: questo analizzatore
 * è generico e scansiona l'intera classe. Rileva sia INVOKEVIRTUAL (tipo
 * astratto) che INVOKEINTERFACE (interfaccia), coprendo tutti i pattern
 * che richiedono delega a un campo di tipo interfaccia o classe astratta.
 *
 * Gestisce anche la delega indiretta nello stesso file .class: se il metodo
 * principale chiama un metodo helper privato che a sua volta chiama il tipo
 * target, la chiamata viene rilevata perché entrambi i metodi appartengono
 * alla stessa classe e vengono scansionati.
 *
 * <p>Risale inoltre la gerarchia delle superclassi se la classe analizzata non
 * contiene la chiamata: su codice reale la delega può essere implementata in una
 * superclasse comune e mai sovrascritta dalla sottoclasse concreta passata al
 * verifier (es. JHotDraw: {@code CompositeFigure.draw()} invoca metodi su {@code
 * Component}, ma {@code GroupFigure} — sottoclasse concreta — non sovrascrive
 * {@code draw()}). Si ferma prima di java.lang.Object e delle classi di libreria
 * (pacchetti java. e javax.), stessa regola usata da {@link ClassAnalyzer}.
 */
public class MethodInvocationAnalyzer extends ClassVisitor {

    private final Set<String> invokedOwnersInternal = new HashSet<>();
    private String superClassInternal;

    private MethodInvocationAnalyzer() {
        super(Opcodes.ASM9);
    }

    @Override
    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces) {
        this.superClassInternal = superName;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                     String signature, String[] exceptions) {
        return new InvocationScanner();
    }

    private class InvocationScanner extends MethodVisitor {
        InvocationScanner() { super(Opcodes.ASM9); }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name,
                                    String descriptor, boolean isInterface) {
            if (opcode == Opcodes.INVOKEVIRTUAL || opcode == Opcodes.INVOKEINTERFACE) {
                invokedOwnersInternal.add(owner);
            }
        }

        // Rileva method reference (es. FileSystemItem::getSize) che il compilatore
        // Java rappresenta come INVOKEDYNAMIC con un Handle nel bootstrap argument
        // che punta direttamente al metodo referenziato — senza generare un metodo
        // sintetico nella stessa .class come fa invece una lambda con corpo esplicito.
        @Override
        public void visitInvokeDynamicInsn(String name, String descriptor,
                                           Handle bootstrapMethodHandle,
                                           Object... bootstrapMethodArguments) {
            for (Object arg : bootstrapMethodArguments) {
                if (arg instanceof Handle) {
                    invokedOwnersInternal.add(((Handle) arg).getOwner());
                }
            }
        }
    }

    /**
     * Ritorna true se la classe identificata da className, o una delle sue superclassi, contiene
     * almeno una chiamata a un metodo (INVOKEVIRTUAL/INVOKEINTERFACE, o method reference via
     * INVOKEDYNAMIC) il cui receiver è di tipo targetTypeName <b>o di un suo sottotipo</b>.
     *
     * <p>Il confronto è per assegnabilità, non per uguaglianza esatta del nome: una delega
     * eseguita su un sottotipo del ruolo dichiarato (es. {@code ((TextFigure) owner()).getFont()}
     * dove il Target è {@code Figure}) conta come delega al Target. Vedi {@link TypeHierarchy} per
     * la motivazione e gli esempi.
     *
     * @param className      nome fully-qualified della classe da analizzare
     * @param targetTypeName nome fully-qualified del tipo target (es. "com.example.LightState")
     */
    public static boolean invokesMethodsOn(String className, String targetTypeName) {
        Set<String> invokedOwners = new HashSet<>();

        ScanResult current = scanOneClass(className, true);
        invokedOwners.addAll(current.invokedOwnersInternal);

        String superClassName = current.superClassName;
        while (superClassName != null && !isOutOfHierarchyScope(superClassName)) {
            ScanResult ancestor = scanOneClass(superClassName, false);
            if (ancestor == null) {
                break; // superclasse non caricabile (es. libreria esterna non sul classpath): ci si ferma qui
            }
            invokedOwners.addAll(ancestor.invokedOwnersInternal);
            superClassName = ancestor.superClassName;
        }

        for (String ownerInternal : invokedOwners) {
            if (TypeHierarchy.isAssignable(ownerInternal.replace('/', '.'), targetTypeName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Legge il bytecode di una classe e raccoglie i tipi (interni) su cui invoca metodi.
     *
     * @param required se true, l'assenza del bytecode è un errore (usato per la classe
     *                 principale passata dal chiamante); se false, ritorna null e lascia
     *                 che invokesMethodsOn si fermi silenziosamente (usato per gli antenati)
     */
    private static ScanResult scanOneClass(String className, boolean required) {
        String resourcePath = className.replace('.', '/') + ".class";
        try (InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                if (required) {
                    throw new IllegalArgumentException("Bytecode non trovato per: " + className);
                }
                return null;
            }
            ClassReader reader = new ClassReader(is);
            MethodInvocationAnalyzer analyzer = new MethodInvocationAnalyzer();
            reader.accept(analyzer, ClassReader.SKIP_FRAMES);
            String superFullyQualified = analyzer.superClassInternal != null
                    ? analyzer.superClassInternal.replace('/', '.')
                    : null;
            return new ScanResult(analyzer.invokedOwnersInternal, superFullyQualified);
        } catch (IOException e) {
            if (required) {
                throw new RuntimeException("Errore nella lettura del bytecode di " + className, e);
            }
            return null;
        }
    }

    private static boolean isOutOfHierarchyScope(String className) {
        return className.equals("java.lang.Object")
                || className.startsWith("java.")
                || className.startsWith("javax.");
    }

    private static class ScanResult {
        final Set<String> invokedOwnersInternal;
        final String superClassName;

        ScanResult(Set<String> invokedOwnersInternal, String superClassName) {
            this.invokedOwnersInternal = invokedOwnersInternal;
            this.superClassName = superClassName;
        }
    }
}
