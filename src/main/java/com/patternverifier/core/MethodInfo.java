package com.patternverifier.core;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MethodInfo {

    private final String name;
    private final String descriptor;
    private final int access;

    public MethodInfo(String name, String descriptor, int access) {
        this.name = name;
        this.descriptor = descriptor;
        this.access = access;
    }

    public String getName() { return name; }
    public String getDescriptor() { return descriptor; }

    public boolean isPrivate()   { return (access & Opcodes.ACC_PRIVATE)   != 0; }
    public boolean isStatic()    { return (access & Opcodes.ACC_STATIC)    != 0; }
    public boolean isAbstract()  { return (access & Opcodes.ACC_ABSTRACT)  != 0; }
    public boolean isPublic()    { return (access & Opcodes.ACC_PUBLIC)    != 0; }

    public boolean isConstructor() { return name.equals("<init>"); }

    /**
     * Restituisce il nome del tipo di ritorno in formato leggibile.
     * Usa la classe Type di ASM che sa già parsare i descrittori di metodo.
     */
    public String getReturnTypeName() {
        return Type.getReturnType(descriptor).getClassName();
    }

    public List<String> getParameterTypeNames() {
        return Arrays.stream(Type.getArgumentTypes(descriptor))
                .map(Type::getClassName)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return name + descriptor;
    }
}
