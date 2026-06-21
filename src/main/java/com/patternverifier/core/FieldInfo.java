package com.patternverifier.core;

import org.objectweb.asm.Opcodes;

public class FieldInfo {

    private final String name;
    private final String descriptor;
    private final int access;

    public FieldInfo(String name, String descriptor, int access) {
        this.name = name;
        this.descriptor = descriptor;
        this.access = access;
    }

    public String getName() { return name; }
    public String getDescriptor() { return descriptor; }

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
