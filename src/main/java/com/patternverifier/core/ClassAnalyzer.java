package com.patternverifier.core;

import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
     * Legge il bytecode della classe dal classpath e restituisce i suoi metadati,
     * arricchiti con campi, metodi e interfacce ereditati dalla gerarchia delle superclassi.
     * Funziona su qualsiasi classe già compilata e caricata dalla JVM.
     *
     * <p>Perché il merge con gli antenati: ASM legge un solo file .class alla volta — un
     * campo o un metodo dichiarato solo in una superclasse (mai ridichiarato nella
     * sottoclasse) è invisibile analizzando la sola sottoclasse. Su fixture scritte apposta
     * per il tool questo non emerge mai (i campi del pattern stanno sempre sulla classe
     * analizzata), ma su codice reale con gerarchie stratificate (es. JHotDraw:
     * {@code EllipseFigure extends AttributeFigure extends AbstractFigure}) i campi e i
     * metodi rilevanti per il pattern vivono spesso in un antenato, non nella foglia.
     */
    public static ClassMetadata analyze(Class<?> clazz) {
        ClassLoader loader = clazz.getClassLoader();
        ClassMetadata own = analyzeResource(loader, clazz.getName());
        return mergeWithAncestors(loader, own);
    }

    private static ClassMetadata analyzeResource(ClassLoader loader, String fullyQualifiedName) {
        String resourcePath = fullyQualifiedName.replace('.', '/') + ".class";
        try (InputStream is = loader.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Bytecode non trovato per: " + fullyQualifiedName);
            }
            ClassReader reader = new ClassReader(is);
            ClassAnalyzer analyzer = new ClassAnalyzer();
            reader.accept(analyzer, ClassReader.SKIP_FRAMES);
            return analyzer.getMetadata();
        } catch (IOException e) {
            throw new RuntimeException("Errore nella lettura del bytecode di " + fullyQualifiedName, e);
        }
    }

    /**
     * Risale la catena delle superclassi unendo campi, metodi e interfacce ereditati in
     * un'unica ClassMetadata "effettiva". Si ferma prima di java.lang.Object e delle classi
     * di libreria (pacchetti java. e javax.): risalire fin lì farebbe emergere metodi come
     * Object.notify() e Object.wait() che farebbero scattare falsi positivi sui verifier
     * che cercano metodi con prefisso notify o wait (es. ObserverVerifier).
     */
    private static ClassMetadata mergeWithAncestors(ClassLoader loader, ClassMetadata own) {
        List<FieldInfo> mergedFields = new ArrayList<>(own.getFields());
        List<MethodInfo> mergedMethods = new ArrayList<>(own.getMethods());
        Set<String> mergedInterfaces = new LinkedHashSet<>(own.getInterfaces());
        List<String> superClassChain = new ArrayList<>();

        String superClassName = own.getSuperClassName();
        while (superClassName != null && !isOutOfHierarchyScope(superClassName)) {
            superClassChain.add(superClassName);
            ClassMetadata ancestor;
            try {
                ancestor = analyzeResource(loader, superClassName);
            } catch (RuntimeException e) {
                break; // superclasse non caricabile (es. libreria esterna non sul classpath): ci si ferma qui
            }
            mergeFields(mergedFields, ancestor.getFields());
            mergeMethods(mergedMethods, ancestor.getMethods());
            mergedInterfaces.addAll(ancestor.getInterfaces());
            superClassName = ancestor.getSuperClassName();
        }

        expandInterfaceHierarchy(loader, mergedInterfaces);

        return new ClassMetadata(own.getClassName(), own.getSuperClassName(),
                new ArrayList<>(mergedInterfaces), own.getAccess(), mergedFields, mergedMethods,
                superClassChain);
    }

    private static boolean isOutOfHierarchyScope(String className) {
        return className.equals("java.lang.Object")
                || className.startsWith("java.")
                || className.startsWith("javax.");
    }

    /**
     * Aggiunge i campi dell'antenato non già presenti (per nome e tipo) nella sottoclasse.
     *
     * <p>Il confronto include il tipo, non solo il nome: se una sottoclasse dichiara un proprio
     * campo con lo stesso nome ma un tipo diverso da un campo dell'antenato (es. {@code
     * RadiusHandle.fOwner} di tipo {@code RoundRectangleFigure} contro {@code
     * AbstractHandle.fOwner} di tipo {@code Figure}), i due campi coesistono realmente in memoria
     * (slot distinti, uno per classe) — scartare quello dell'antenato per solo nome uguale
     * nasconderebbe un campo che i verifier potrebbero legittimamente cercare.
     */
    private static void mergeFields(List<FieldInfo> target, List<FieldInfo> ancestorFields) {
        for (FieldInfo candidate : ancestorFields) {
            boolean alreadyPresent = target.stream()
                    .anyMatch(existing -> existing.getName().equals(candidate.getName())
                            && existing.getDescriptor().equals(candidate.getDescriptor()));
            if (!alreadyPresent) {
                target.add(candidate);
            }
        }
    }

    /**
     * Aggiunge i metodi dell'antenato non già presenti (per nome+descrittore) nella
     * sottoclasse. I costruttori non si ereditano in Java e vengono esclusi esplicitamente.
     */
    private static void mergeMethods(List<MethodInfo> target, List<MethodInfo> ancestorMethods) {
        for (MethodInfo candidate : ancestorMethods) {
            if (candidate.isConstructor()) {
                continue;
            }
            boolean alreadyOverridden = target.stream()
                    .anyMatch(existing -> existing.getName().equals(candidate.getName())
                            && existing.getDescriptor().equals(candidate.getDescriptor()));
            if (!alreadyOverridden) {
                target.add(candidate);
            }
        }
    }

    /**
     * Espande transitivamente le interfacce raccolte con quelle che esse stesse estendono
     * (es. {@code ConnectionFigure extends Figure, FigureChangeListener}), leggendone il
     * bytecode allo stesso modo delle classi.
     */
    private static void expandInterfaceHierarchy(ClassLoader loader, Set<String> interfaces) {
        Deque<String> toProcess = new ArrayDeque<>(interfaces);
        Set<String> processed = new HashSet<>();
        while (!toProcess.isEmpty()) {
            String iface = toProcess.poll();
            if (!processed.add(iface) || isOutOfHierarchyScope(iface)) {
                continue;
            }
            try {
                ClassMetadata ifaceMetadata = analyzeResource(loader, iface);
                for (String parentInterface : ifaceMetadata.getInterfaces()) {
                    if (interfaces.add(parentInterface)) {
                        toProcess.add(parentInterface);
                    }
                }
            } catch (RuntimeException e) {
                // interfaccia non caricabile (es. libreria esterna): si ignora, non blocca l'analisi
            }
        }
    }
}
