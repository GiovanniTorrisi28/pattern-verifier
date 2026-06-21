package com.patternverifier.assertions;

import com.patternverifier.core.ClassAnalyzer;
import com.patternverifier.core.ClassMetadata;
import com.patternverifier.verifiers.DecoratorVerifier;

import java.util.List;

public class DecoratorAssert {

    private final Class<?> decoratorClass;
    private final ClassMetadata decoratorMetadata;

    public DecoratorAssert(Class<?> decoratorClass, ClassMetadata decoratorMetadata) {
        this.decoratorClass = decoratorClass;
        this.decoratorMetadata = decoratorMetadata;
    }

    /**
     * Specifica il Component e scatta la verifica. Metodo terminale della catena.
     */
    public void forComponent(Class<?> component) {
        ClassMetadata componentMetadata = ClassAnalyzer.analyze(component);

        List<String> violations = new DecoratorVerifier(decoratorMetadata, componentMetadata).verify();
        if (!violations.isEmpty()) {
            throw new AssertionError(formatViolations(component, violations));
        }
    }

    private String formatViolations(Class<?> component, List<String> violations) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(decoratorClass.getSimpleName())
          .append(": violazione pattern Decorator")
          .append(" (Component=").append(component.getSimpleName()).append(")\n");
        violations.forEach(v -> sb.append("  - ").append(v).append("\n"));
        return sb.toString();
    }
}
