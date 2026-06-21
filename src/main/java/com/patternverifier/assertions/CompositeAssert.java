package com.patternverifier.assertions;

import com.patternverifier.core.ClassAnalyzer;
import com.patternverifier.core.ClassMetadata;
import com.patternverifier.verifiers.CompositeVerifier;

import java.util.List;

public class CompositeAssert {

    private final Class<?> compositeClass;
    private final ClassMetadata compositeMetadata;

    public CompositeAssert(Class<?> compositeClass, ClassMetadata compositeMetadata) {
        this.compositeClass = compositeClass;
        this.compositeMetadata = compositeMetadata;
    }

    public void forComponent(Class<?> component) {
        ClassMetadata componentMetadata = ClassAnalyzer.analyze(component);

        List<String> violations = new CompositeVerifier(compositeMetadata, componentMetadata).verify();
        if (!violations.isEmpty()) {
            throw new AssertionError(formatViolations(component, violations));
        }
    }

    private String formatViolations(Class<?> component, List<String> violations) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(compositeClass.getSimpleName())
          .append(": violazione pattern Composite")
          .append(" (Component=").append(component.getSimpleName()).append(")\n");
        violations.forEach(v -> sb.append("  - ").append(v).append("\n"));
        return sb.toString();
    }
}
