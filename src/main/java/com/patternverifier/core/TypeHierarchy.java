package com.patternverifier.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Risoluzione di assegnabilità tra tipi via bytecode: dato un tipo, ne calcola l'insieme dei
 * supertipi (superclassi + interfacce, transitivi). Serve ai verifier per riconoscere una
 * relazione "X è-un Target" quando il codice usa un <b>sottotipo</b> di Target invece del tipo
 * esatto dichiarato nel ruolo del pattern.
 *
 * <p>Motivazione: prima di questa classe i verifier confrontavano i nomi dei tipi con
 * {@code equals()} — un match nominale esatto che mancava le relazioni via sottotipo, producendo
 * falsi negativi su codice reale conforme. Esempi verificati su JHotDraw 5.1
 * (vedi {@code docs/jhotdraw_analisi_fallimenti.md}):
 * <ul>
 *   <li>{@code TextFigure} ha un campo {@code OffsetLocator} (che implementa {@code Locator})
 *       dove il ruolo Strategy atteso è {@code Locator};</li>
 *   <li>diversi {@code Handle} delegano al proprio {@code Figure} tramite un cast a un sottotipo
 *       ({@code TextFigure}, {@code PolyLineFigure}...) necessario per invocare metodi non
 *       esposti dall'interfaccia {@code Figure}.</li>
 * </ul>
 *
 * <p>Il calcolo si ferma prima di {@code java.lang.Object} e dei pacchetti {@code java.}/
 * {@code javax.} (non vi ricorre, gli antenati rilevanti per i ruoli GoF sono tipi di dominio) e
 * degrada in modo sicuro se un bytecode non è caricabile (ritorna quel che ha raccolto). I
 * risultati sono cache-ati per tipo.
 */
public final class TypeHierarchy {

    private TypeHierarchy() {}

    /** nome FQN del tipo -> insieme dei suoi supertipi (FQN), esclusa la classe stessa. */
    private static final Map<String, Set<String>> SUPERTYPES = new ConcurrentHashMap<>();

    /**
     * Ritorna true se {@code subtypeFqn} coincide con {@code targetFqn} oppure {@code targetFqn}
     * è un supertipo (superclasse o interfaccia, a qualsiasi livello) di {@code subtypeFqn}.
     * Entrambi i nomi in forma FQN con punti (es. {@code "CH.ifa.draw.figures.TextFigure"}).
     */
    public static boolean isAssignable(String subtypeFqn, String targetFqn) {
        if (subtypeFqn.equals(targetFqn)) {
            return true;
        }
        return supertypesOf(subtypeFqn).contains(targetFqn);
    }

    private static Set<String> supertypesOf(String typeFqn) {
        return SUPERTYPES.computeIfAbsent(typeFqn, TypeHierarchy::computeSupertypes);
    }

    private static Set<String> computeSupertypes(String typeFqn) {
        Set<String> acc = new HashSet<>();
        collect(typeFqn, acc);
        return acc;
    }

    private static void collect(String typeFqn, Set<String> acc) {
        for (String sup : directSupertypes(typeFqn)) {
            if (acc.add(sup) && !isOutOfScope(sup)) {
                collect(sup, acc);
            }
        }
    }

    /** Superclasse diretta + interfacce dirette, in FQN. Vuoto se il bytecode non è caricabile. */
    private static List<String> directSupertypes(String typeFqn) {
        String resourcePath = typeFqn.replace('.', '/') + ".class";
        try (InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                return List.of();
            }
            ClassReader reader = new ClassReader(is);
            SupertypeCollector collector = new SupertypeCollector();
            reader.accept(collector, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            return collector.supertypes();
        } catch (IOException e) {
            return List.of();
        }
    }

    private static boolean isOutOfScope(String fqn) {
        return fqn.equals("java.lang.Object") || fqn.startsWith("java.") || fqn.startsWith("javax.");
    }

    private static final class SupertypeCollector extends ClassVisitor {
        private String superName;
        private String[] interfaces = new String[0];

        SupertypeCollector() { super(Opcodes.ASM9); }

        @Override
        public void visit(int version, int access, String name, String signature,
                          String superName, String[] interfaces) {
            this.superName = superName;
            this.interfaces = interfaces != null ? interfaces : new String[0];
        }

        List<String> supertypes() {
            List<String> all = new ArrayList<>();
            if (superName != null) {
                all.add(superName.replace('/', '.'));
            }
            for (String i : interfaces) {
                all.add(i.replace('/', '.'));
            }
            return all;
        }
    }
}
