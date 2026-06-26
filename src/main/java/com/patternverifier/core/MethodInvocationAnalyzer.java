package com.patternverifier.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.InputStream;

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
 */
public class MethodInvocationAnalyzer extends ClassVisitor {

    private final String targetTypeInternal;
    private boolean found = false;

    private MethodInvocationAnalyzer(String targetTypeInternal) {
        super(Opcodes.ASM9);
        this.targetTypeInternal = targetTypeInternal;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                     String signature, String[] exceptions) {
        if (found) return null;
        return new InvocationScanner();
    }

    private class InvocationScanner extends MethodVisitor {
        InvocationScanner() { super(Opcodes.ASM9); }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name,
                                    String descriptor, boolean isInterface) {
            if ((opcode == Opcodes.INVOKEVIRTUAL || opcode == Opcodes.INVOKEINTERFACE)
                    && owner.equals(targetTypeInternal)) {
                found = true;
            }
        }
    }

    /**
     * Ritorna true se la classe identificata da className contiene almeno
     * una chiamata a un metodo (INVOKEVIRTUAL o INVOKEINTERFACE) il cui
     * receiver è di tipo targetTypeName.
     *
     * @param className      nome fully-qualified della classe da analizzare
     * @param targetTypeName nome fully-qualified del tipo target (es. "com.example.LightState")
     */
    public static boolean invokesMethodsOn(String className, String targetTypeName) {
        String resourcePath = className.replace('.', '/') + ".class";
        String targetInternal = targetTypeName.replace('.', '/');
        try (InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Bytecode non trovato per: " + className);
            }
            ClassReader reader = new ClassReader(is);
            MethodInvocationAnalyzer analyzer = new MethodInvocationAnalyzer(targetInternal);
            reader.accept(analyzer, ClassReader.SKIP_FRAMES);
            return analyzer.found;
        } catch (IOException e) {
            throw new RuntimeException("Errore nella lettura del bytecode di " + className, e);
        }
    }
}
