package com.patternverifier.verifiers;

import com.patternverifier.core.ClassMetadata;
import com.patternverifier.core.MethodInvocationAnalyzer;
import com.patternverifier.core.TypeHierarchy;

import java.util.ArrayList;
import java.util.List;

public class DecoratorVerifier {

    private final ClassMetadata decorator;
    private final ClassMetadata component;

    public DecoratorVerifier(ClassMetadata decorator, ClassMetadata component) {
        this.decorator = decorator;
        this.component = component;
    }

    public List<String> verify() {
        List<String> violations = new ArrayList<>();
        checkDecoratorImplementsComponent(violations);
        checkDecoratorHasComponentField(violations);
        checkConstructorAcceptsComponent(violations);
        checkDecoratorDelegatesToComponent(violations);
        return violations;
    }

    private void checkDecoratorDelegatesToComponent(List<String> violations) {
        if (!MethodInvocationAnalyzer.invokesMethodsOn(decorator.getClassName(), component.getClassName())) {
            violations.add(decorator.getSimpleName()
                    + " non delega mai al Component "
                    + component.getSimpleName()
                    + " — il Decorator deve chiamare i metodi del Component wrappato");
        }
    }

    private void checkDecoratorImplementsComponent(List<String> violations) {
        // Il Component GoF è spesso una classe astratta (non solo un'interfaccia):
        // TypeHierarchy.isAssignable copre entrambi i casi.
        if (!TypeHierarchy.isAssignable(decorator.getClassName(), component.getClassName())) {
            violations.add(
                decorator.getSimpleName() + " non implementa né estende " + component.getSimpleName() +
                " — il Decorator deve conformarsi allo stesso tipo (interfaccia o classe astratta) del Component"
            );
        }
    }

    private void checkDecoratorHasComponentField(List<String> violations) {
        // Il campo deve essere del tipo dell'interfaccia, non di una classe concreta.
        // Questo è ciò che distingue il Decorator dal Proxy.
        boolean hasField = decorator.getFields().stream()
                .anyMatch(f -> f.getTypeName().equals(component.getClassName()));
        if (!hasField) {
            violations.add(
                decorator.getSimpleName() + " non ha un campo di tipo " + component.getSimpleName() +
                " — il Decorator deve wrappare il Component tramite il tipo dell'interfaccia, non una classe concreta"
            );
        }
    }

    private void checkConstructorAcceptsComponent(List<String> violations) {
        boolean hasConstructorWithComponent = decorator.getMethods().stream()
                .filter(m -> m.isConstructor())
                .anyMatch(m -> m.getParameterTypeNames().contains(component.getClassName()));
        if (!hasConstructorWithComponent) {
            violations.add(
                decorator.getSimpleName() + " non ha un costruttore che accetta " + component.getSimpleName() +
                " — il Decorator deve ricevere il Component da wrappare tramite costruttore"
            );
        }
    }
}
