package com.patternverifier.verifiers;

import com.patternverifier.core.ClassMetadata;
import com.patternverifier.core.FieldInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CompositeVerifier {

    // Tipi di collezione riconosciuti come campi validi per contenere i figli
    private static final Set<String> COLLECTION_TYPES = Set.of(
        "java.util.List",       "java.util.ArrayList",    "java.util.LinkedList",
        "java.util.Set",        "java.util.HashSet",      "java.util.LinkedHashSet",
        "java.util.TreeSet",    "java.util.Collection"
    );

    private final ClassMetadata composite;
    private final ClassMetadata component;

    public CompositeVerifier(ClassMetadata composite, ClassMetadata component) {
        this.composite = composite;
        this.component = component;
    }

    public List<String> verify() {
        List<String> violations = new ArrayList<>();
        checkCompositeImplementsComponent(violations);
        checkHasCollectionField(violations);
        checkHasAddMethod(violations);
        return violations;
    }

    private void checkCompositeImplementsComponent(List<String> violations) {
        if (!composite.getInterfaces().contains(component.getClassName())) {
            violations.add(
                composite.getSimpleName() + " non implementa l'interfaccia " + component.getSimpleName() +
                " — il Composite deve implementare la stessa interfaccia del Component"
            );
        }
    }

    private void checkHasCollectionField(List<String> violations) {
        // ASM vede solo il tipo raw della collezione (type erasure elimina il tipo generico).
        // Verifichiamo che esista almeno un campo di tipo Collection noto.
        boolean hasCollectionField = composite.getFields().stream()
                .map(FieldInfo::getTypeName)
                .anyMatch(COLLECTION_TYPES::contains);
        if (!hasCollectionField) {
            violations.add(
                composite.getSimpleName() + " non ha un campo di tipo Collection (List, Set, ecc.) " +
                "— il Composite deve mantenere una collezione di figli di tipo " + component.getSimpleName() +
                " (nota: il tipo generico non è verificabile per via della type erasure)"
            );
        }
    }

    private void checkHasAddMethod(List<String> violations) {
        // Verifica che esista almeno un metodo il cui nome inizia con "add"
        // e che accetta il tipo Component come parametro.
        boolean hasAddMethod = composite.getMethods().stream()
                .filter(m -> m.getName().startsWith("add"))
                .anyMatch(m -> m.getParameterTypeNames().contains(component.getClassName()));
        if (!hasAddMethod) {
            violations.add(
                composite.getSimpleName() + " non ha un metodo add*(" + component.getSimpleName() + ")" +
                " — il Composite deve esporre un metodo per aggiungere figli di tipo " + component.getSimpleName()
            );
        }
    }
}
