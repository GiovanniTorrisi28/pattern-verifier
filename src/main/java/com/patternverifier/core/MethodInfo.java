package com.patternverifier.core;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MethodInfo {

    private final String name;
    private final String descriptor;
    private final int access;
    private final String signature;

    public MethodInfo(String name, String descriptor, int access) {
        this(name, descriptor, access, null);
    }

    /**
     * @param signature firma generica del metodo (es. {@code ()Ljava/util/Vector<Lcom/example/Foo;>;}),
     *                  o null se il metodo non usa generics (tipo di ritorno raw, o codice
     *                  pre-Java 5 come JHotDraw 1997 — non ha mai un attributo Signature).
     */
    public MethodInfo(String name, String descriptor, int access, String signature) {
        this.name = name;
        this.descriptor = descriptor;
        this.access = access;
        this.signature = signature;
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

    /**
     * Estrae il tipo dell'argomento generico del valore di ritorno, quando questo è una
     * Collection a singolo parametro (es. {@code Vector<Handle> handles()} → {@code
     * "com.example.Handle"}), leggendo l'attributo Signature del bytecode. Restituisce null se
     * il metodo non ha firma generica (tipo di ritorno raw — normale per codice pre-generics,
     * come {@code Vector handles()} in JHotDraw 1997) o se la firma non ha un argomento di tipo
     * classe riconoscibile.
     *
     * <p>Usato da {@link com.patternverifier.verifiers.FactoryMethodVerifier} per riconoscere la
     * variante "a lotti" del Factory Method (un metodo che restituisce una collezione di Product
     * anziché un singolo Product). Se il tipo di ritorno è raw, l'informazione sull'elemento
     * non esiste nel bytecode e questo metodo restituisce null: a differenza di {@link
     * FieldInfo#getGenericElementTypeName()} (dove l'assenza di generics porta ad accettare il
     * campo, perché altri controlli del pattern corroborano già la corrispondenza), qui
     * l'elemento generico è l'unico segnale che distingue "restituisce Product" da "restituisce
     * una collezione qualsiasi" — in sua assenza il chiamante deve trattare il match come non
     * confermato, non come implicitamente valido.
     */
    public String getGenericReturnElementTypeName() {
        if (signature == null) {
            return null;
        }
        GenericReturnElementExtractor extractor = new GenericReturnElementExtractor();
        new SignatureReader(signature).accept(extractor);
        return extractor.elementType;
    }

    private static class GenericReturnElementExtractor extends SignatureVisitor {
        String elementType;

        GenericReturnElementExtractor() { super(Opcodes.ASM9); }

        @Override
        public SignatureVisitor visitReturnType() {
            return new SignatureVisitor(Opcodes.ASM9) {
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
            };
        }
    }

    @Override
    public String toString() {
        return name + descriptor;
    }
}
