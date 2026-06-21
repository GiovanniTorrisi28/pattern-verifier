package com.patternverifier.assertions;

import com.patternverifier.core.ClassAnalyzer;
import com.patternverifier.core.ClassMetadata;
import com.patternverifier.verifiers.BridgeVerifier;

import java.util.List;

public class BridgeAssert {

    private final Class<?> abstractionClass;
    private final ClassMetadata abstractionMetadata;

    public BridgeAssert(Class<?> clazz, ClassMetadata metadata) {
        this.abstractionClass = clazz;
        this.abstractionMetadata = metadata;
    }

    public void withImplementor(Class<?> implementorClass) {
        ClassMetadata implementorMetadata = ClassAnalyzer.analyze(implementorClass);
        List<String> violations =
                new BridgeVerifier(abstractionMetadata, implementorMetadata).verify();
        if (!violations.isEmpty()) {
            throw new AssertionError(formatViolations(implementorClass, violations));
        }
    }

    private String formatViolations(Class<?> implementorClass, List<String> violations) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(abstractionClass.getSimpleName())
          .append(" / ").append(implementorClass.getSimpleName())
          .append(": violazione pattern Bridge\n");
        violations.forEach(v -> sb.append("  - ").append(v).append("\n"));
        return sb.toString();
    }
}
