package com.patternverifier.core;

import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClassAnalyzer extends ClassVisitor {

    private String className;
    private String superClassName;
    private List<String> interfaces;
    private int access;
    private final List<FieldInfo> fields = new ArrayList<>();
    private final List<MethodInfo> methods = new ArrayList<>();

    public ClassAnalyzer() {
        super(Opcodes.ASM9);
    }

    @Override
    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces) {
        this.access = access;
        this.className = name.replace('/', '.');
        this.superClassName = superName != null ? superName.replace('/', '.') : null;
        this.interfaces = Arrays.stream(interfaces)
                .map(i -> i.replace('/', '.'))
                .collect(Collectors.toList());
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor,
                                   String signature, Object value) {
        fields.add(new FieldInfo(name, descriptor, access));
        return null; // non ci interessa visitare il contenuto del campo
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                     String signature, String[] exceptions) {
        methods.add(new MethodInfo(name, descriptor, access));
        return null; // per ora non analizziamo il corpo dei metodi
    }

    public ClassMetadata getMetadata() {
        return new ClassMetadata(className, superClassName, interfaces, access, fields, methods);
    }

    /**
     * Legge il bytecode della classe dal classpath e restituisce i suoi metadati.
     * Funziona su qualsiasi classe già compilata e caricata dalla JVM.
     */
    public static ClassMetadata analyze(Class<?> clazz) {
        String resourcePath = clazz.getName().replace('.', '/') + ".class";
        try (InputStream is = clazz.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Bytecode non trovato per: " + clazz.getName());
            }
            ClassReader reader = new ClassReader(is);
            ClassAnalyzer analyzer = new ClassAnalyzer();
            reader.accept(analyzer, ClassReader.SKIP_FRAMES);
            return analyzer.getMetadata();
        } catch (IOException e) {
            throw new RuntimeException("Errore nella lettura del bytecode di " + clazz.getName(), e);
        }
    }
}
