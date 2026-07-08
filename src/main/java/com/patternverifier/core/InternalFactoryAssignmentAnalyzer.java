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

/**
 * Distingue un metodo "factory interno" che alimenta lo stato della classe (Strategy/State
 * genuini, es. {@code SelectionTool.mouseDown()}: {@code fChild = createHandleTracker(...)}) da
 * un metodo che restituisce lo stesso tipo ma lo consegna a un chiamante esterno senza mai
 * conservarlo (Factory Method puro, es. {@code AbstractFigure.connectorAt()}:
 * {@code return new ChopBoxConnector(this)}, mai assegnato a un campo).
 *
 * <p>Scansiona tutti i metodi della classe cercando la sequenza di istruzioni: una chiamata a
 * un proprio metodo (INVOKEVIRTUAL o INVOKESPECIAL con owner uguale alla classe stessa) seguita
 * immediatamente da un'assegnazione a un proprio campo (PUTFIELD) di tipo assegnabile al target.
 * È la forma bytecode tipica generata da javac per un'assegnazione diretta come
 * {@code campo = metodoProprio(...);}.
 *
 * <p>Euristica basata sull'adiacenza delle istruzioni, non dataflow generale: non riconosce
 * varianti con indirection tramite variabile locale intermedia o cast esplicito tra la chiamata
 * e l'assegnazione. Falso-negativo possibile (violazione ancora segnalata quando il codice reale
 * sarebbe conforme); nessun rischio di falso-positivo, perché la sequenza riconosciuta implica
 * sempre un'assegnazione realmente presente nel bytecode.
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
        private boolean pendingSelfInvoke = false;

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

        @Override public void visitInsn(int opcode) { pendingSelfInvoke = false; }
        @Override public void visitIntInsn(int opcode, int operand) { pendingSelfInvoke = false; }
        @Override public void visitVarInsn(int opcode, int var) { pendingSelfInvoke = false; }
        @Override public void visitTypeInsn(int opcode, String type) { pendingSelfInvoke = false; }
        @Override public void visitJumpInsn(int opcode, Label label) { pendingSelfInvoke = false; }
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

        @Override
        public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
            pendingSelfInvoke = false;
        }

        @Override
        public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
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
