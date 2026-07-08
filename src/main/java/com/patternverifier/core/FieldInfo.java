package com.patternverifier.core;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

public class FieldInfo {

    private final String name;
    private final String descriptor;
    private final int access;
    private final String signature;

    public FieldInfo(String name, String descriptor, int access) {
        this(name, descriptor, access, null);
    }

    /**
     * @param signature firma generica del campo (es. {@code Ljava/util/List<Lcom/example/Foo;>;}),
     *                  o null se il campo non usa generics (tipo raw, o codice pre-Java 5 come
     *                  JHotDraw 1997 — non ha mai un attributo Signature).
     */
    public FieldInfo(String name, String descriptor, int access, String signature) {
        this.name = name;
        this.descriptor = descriptor;
        this.access = access;
        this.signature = signature;
    }

    public String getName() { return name; }
    public String getDescriptor() { return descriptor; }
    public String getSignature() { return signature; }

    /**
     * Estrae il tipo dell'argomento generico di un campo Collection a singolo parametro
     * (es. {@code List<Foo>} → {@code "com.example.Foo"}), leggendo l'attributo Signature del
     * bytecode. Restituisce null se il campo non ha firma generica (tipo raw — normale per
     * codice pre-generics) o se la firma non ha un argomento di tipo classe riconoscibile.
     *
     * <p>Usato per verificare che una Collection dichiarata come campo di un pattern (Composite,
     * Observer) contenga davvero il tipo atteso (Component, Observer) e non un tipo qualunque —
     * il solo controllo sul tipo raw (type erasure) non può distinguere {@code List<Figure>} da
     * {@code List<Point>}.
     */
    public String getGenericElementTypeName() {
        if (signature == null) {
            return null;
        }
        GenericElementExtractor extractor = new GenericElementExtractor();
        new SignatureReader(signature).acceptType(extractor);
        return extractor.elementType;
    }

    private static class GenericElementExtractor extends SignatureVisitor {
        String elementType;

        GenericElementExtractor() { super(Opcodes.ASM9); }

        @Override
        public SignatureVisitor visitTypeArgument(char wildcard) {
            return new SignatureVisitor(Opcodes.ASM9) {
                @Override
                public void visitClassType(String name) {
                    if (elementType == null) {
                        elementType = name.replace('/', '.');
                    }
                }
            };
        }
    }

    public boolean isPrivate() { return (access & Opcodes.ACC_PRIVATE) != 0; }
    public boolean isStatic()  { return (access & Opcodes.ACC_STATIC)  != 0; }
    public boolean isFinal()   { return (access & Opcodes.ACC_FINAL)   != 0; }

    /**
     * Restituisce il nome del tipo del campo in formato leggibile (es. "com.example.MyClass").
     * I tipi oggetto in ASM hanno formato "Lcom/example/MyClass;" — rimuoviamo L e ; e sostituiamo /.
     */
    public String getTypeName() {
        if (descriptor.startsWith("L") && descriptor.endsWith(";")) {
            return descriptor.substring(1, descriptor.length() - 1).replace('/', '.');
        }
        return switch (descriptor) {
            case "I" -> "int";
            case "J" -> "long";
            case "D" -> "double";
            case "F" -> "float";
            case "Z" -> "boolean";
            case "B" -> "byte";
            case "C" -> "char";
            case "S" -> "short";
            default  -> descriptor;
        };
    }

    @Override
    public String toString() {
        return getTypeName() + " " + name;
    }
}
