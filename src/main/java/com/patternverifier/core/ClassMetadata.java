package com.patternverifier.core;

import org.objectweb.asm.Opcodes;

import java.util.List;

public class ClassMetadata {

    private final String className;
    private final String superClassName;
    private final List<String> interfaces;
    private final int access;
    private final List<FieldInfo> fields;
    private final List<MethodInfo> methods;

    public ClassMetadata(String className, String superClassName, List<String> interfaces,
                         int access, List<FieldInfo> fields, List<MethodInfo> methods) {
        this.className = className;
        this.superClassName = superClassName;
        this.interfaces = interfaces;
        this.access = access;
        this.fields = fields;
        this.methods = methods;
    }

    public String getClassName()      { return className; }
    public String getSuperClassName() { return superClassName; }
    public List<String> getInterfaces() { return interfaces; }
    public List<FieldInfo> getFields()   { return fields; }
    public List<MethodInfo> getMethods() { return methods; }

    public boolean isAbstract()  { return (access & Opcodes.ACC_ABSTRACT)  != 0; }
    public boolean isInterface() { return (access & Opcodes.ACC_INTERFACE)  != 0; }
    public boolean isFinal()     { return (access & Opcodes.ACC_FINAL)     != 0; }

    public String getSimpleName() {
        int dot = className.lastIndexOf('.');
        return dot >= 0 ? className.substring(dot + 1) : className;
    }
}
