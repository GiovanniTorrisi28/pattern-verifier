package com.patternverifier.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Analizza il corpo di un metodo specifico e raccoglie i nomi dei metodi
 * invocati via INVOKEVIRTUAL sulla stessa classe.
 *
 * Usato da TemplateMethodVerifier per verificare che il template method
 * chiami effettivamente i passi astratti della stessa classe.
 * È il primo componente del progetto che usa MethodVisitor per l'analisi
 * del corpo di un metodo anziché solo la firma.
 */
public class TemplateMethodBodyAnalyzer extends ClassVisitor {

    private final String targetMethodName;
    private final String internalClassName;
    private final Set<String> calledMethods = new HashSet<>();

    private TemplateMethodBodyAnalyzer(String targetMethodName, String internalClassName) {
        super(Opcodes.ASM9);
        this.targetMethodName = targetMethodName;
        this.internalClassName = internalClassName;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                     String signature, String[] exceptions) {
        boolean isAbstract = (access & Opcodes.ACC_ABSTRACT) != 0;
        if (name.equals(targetMethodName) && !isAbstract) {
            return new MethodCallCollector(calledMethods, internalClassName);
        }
        return null;
    }

    public Set<String> getCalledMethods() {
        return calledMethods;
    }

    /**
     * Carica il bytecode della classe e restituisce i nomi dei metodi
     * invocati via INVOKEVIRTUAL sulla stessa classe nel corpo di methodName.
     */
    public static Set<String> findCalledMethods(Class<?> clazz, String methodName) {
        String internalName = clazz.getName().replace('.', '/');
        String resourcePath = internalName + ".class";
        try (InputStream is = clazz.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Bytecode non trovato per: " + clazz.getName());
            }
            ClassReader reader = new ClassReader(is);
            TemplateMethodBodyAnalyzer analyzer =
                    new TemplateMethodBodyAnalyzer(methodName, internalName);
            reader.accept(analyzer, ClassReader.SKIP_FRAMES);
            return analyzer.getCalledMethods();
        } catch (IOException e) {
            throw new RuntimeException("Errore nella lettura del bytecode di " + clazz.getName(), e);
        }
    }

    // Raccoglie INVOKEVIRTUAL verso la stessa classe — così vengono rilevate
    // solo le chiamate dirette a metodi dell'AbstractClass, escludendo
    // chiamate a metodi di java.lang.Object o di altre classi.
    private static class MethodCallCollector extends MethodVisitor {

        private final Set<String> calledMethods;
        private final String ownerFilter;

        MethodCallCollector(Set<String> calledMethods, String ownerFilter) {
            super(Opcodes.ASM9);
            this.calledMethods = calledMethods;
            this.ownerFilter = ownerFilter;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name,
                                    String descriptor, boolean isInterface) {
            if (opcode == Opcodes.INVOKEVIRTUAL && owner.equals(ownerFilter)) {
                calledMethods.add(name);
            }
        }
    }
}
