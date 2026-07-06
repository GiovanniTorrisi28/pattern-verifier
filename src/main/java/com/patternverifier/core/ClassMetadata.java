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
    private final List<String> superClassChain;

    public ClassMetadata(String className, String superClassName, List<String> interfaces,
                         int access, List<FieldInfo> fields, List<MethodInfo> methods) {
        this(className, superClassName, interfaces, access, fields, methods, List.of());
    }

    /**
     * @param superClassChain tutti gli antenati della classe, dal padre diretto in su, fino al
     *                        punto in cui ClassAnalyzer si ferma (java.lang.Object o pacchetti
     *                        java./javax.). Popolata solo dalla ClassMetadata finale restituita
     *                        da ClassAnalyzer.analyze() dopo il merge con gli antenati — le letture
     *                        intermedie di una singola classe la lasciano vuota, non serve loro.
     */
    public ClassMetadata(String className, String superClassName, List<String> interfaces,
                         int access, List<FieldInfo> fields, List<MethodInfo> methods,
                         List<String> superClassChain) {
        this.className = className;
        this.superClassName = superClassName;
        this.interfaces = interfaces;
        this.access = access;
        this.fields = fields;
        this.methods = methods;
        this.superClassChain = superClassChain;
    }

    public String getClassName()      { return className; }
    public String getSuperClassName() { return superClassName; }
    public List<String> getInterfaces() { return interfaces; }
    public List<FieldInfo> getFields()   { return fields; }
    public List<MethodInfo> getMethods() { return methods; }
    public List<String> getSuperClassChain() { return superClassChain; }

    /**
     * Ritorna true se ancestorClassName è una superclasse di questa classe a qualsiasi
     * livello della gerarchia (non solo il padre diretto) — es. CopyCommand.isDescendantOf(
     * "CH.ifa.draw.util.Command") è true anche se il padre diretto è FigureTransferCommand.
     */
    public boolean isDescendantOf(String ancestorClassName) {
        return superClassChain.contains(ancestorClassName);
    }

    public boolean isAbstract()  { return (access & Opcodes.ACC_ABSTRACT)  != 0; }
    public boolean isInterface() { return (access & Opcodes.ACC_INTERFACE)  != 0; }
    public boolean isFinal()     { return (access & Opcodes.ACC_FINAL)     != 0; }

    /** Flag di accesso grezzo, usato da ClassAnalyzer per ricostruire i metadati dopo il merge con gli antenati. */
    public int getAccess() { return access; }

    public String getSimpleName() {
        int dot = className.lastIndexOf('.');
        return dot >= 0 ? className.substring(dot + 1) : className;
    }
}
