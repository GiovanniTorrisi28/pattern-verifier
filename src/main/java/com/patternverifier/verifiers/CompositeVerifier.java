package com.patternverifier.verifiers;

import com.patternverifier.core.ClassMetadata;
import com.patternverifier.core.CollectionTypes;
import com.patternverifier.core.MethodInvocationAnalyzer;

import java.util.ArrayList;
import java.util.List;

public class CompositeVerifier {

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
        checkCompositeDelegatesToComponent(violations);
        return violations;
    }

    private void checkCompositeDelegatesToComponent(List<String> violations) {
        if (!MethodInvocationAnalyzer.invokesMethodsOn(composite.getClassName(), component.getClassName())) {
            violations.add(composite.getSimpleName()
                    + " non invoca mai metodi sui Component figli di tipo "
                    + component.getSimpleName()
                    + " — il Composite deve delegare le operazioni ai figli");
        }
    }

    private void checkCompositeImplementsComponent(List<String> violations) {
        // Il Component GoF è spesso una classe astratta (non solo un'interfaccia).
        String componentName = component.getClassName();
        boolean conforms = composite.getInterfaces().contains(componentName)
                || componentName.equals(composite.getSuperClassName())
                || composite.isDescendantOf(componentName);
        if (!conforms) {
            violations.add(
                composite.getSimpleName() + " non implementa né estende " + component.getSimpleName() +
                " — il Composite deve conformarsi allo stesso tipo (interfaccia o classe astratta) del Component"
            );
        }
    }

    private void checkHasCollectionField(List<String> violations) {
        // Verifica che esista un campo Collection il cui tipo generico (se dichiarato) sia
        // assegnabile al Component — non una Collection qualunque (vedi CollectionTypes.isCollectionOf).
        String componentName = component.getClassName();
        boolean hasCollectionField = composite.getFields().stream()
                .anyMatch(f -> CollectionTypes.isCollectionOf(f, componentName));
        if (!hasCollectionField) {
            violations.add(
                composite.getSimpleName() + " non ha un campo di tipo Collection di " + component.getSimpleName() +
                " — il Composite deve mantenere una collezione di figli di tipo " + component.getSimpleName() +
                " (il tipo generico è verificato solo se il campo dichiara i generics esplicitamente)"
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
