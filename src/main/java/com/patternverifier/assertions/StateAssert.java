package com.patternverifier.assertions;

import com.patternverifier.core.ClassAnalyzer;
import com.patternverifier.core.ClassMetadata;
import com.patternverifier.verifiers.StateVerifier;

import java.util.List;

public class StateAssert {

    private final Class<?> contextClass;
    private final ClassMetadata contextMetadata;

    public StateAssert(Class<?> clazz, ClassMetadata metadata) {
        this.contextClass = clazz;
        this.contextMetadata = metadata;
    }

    public void withStateInterface(Class<?> stateClass) {
        ClassMetadata stateMetadata = ClassAnalyzer.analyze(stateClass);
        List<String> violations = new StateVerifier(contextMetadata, stateMetadata).verify();
        if (!violations.isEmpty()) {
            throw new AssertionError(formatViolations(stateClass, violations));
        }
    }

    private String formatViolations(Class<?> stateClass, List<String> violations) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(contextClass.getSimpleName())
          .append(" / ").append(stateClass.getSimpleName())
          .append(": violazione pattern State\n");
        violations.forEach(v -> sb.append("  - ").append(v).append("\n"));
        return sb.toString();
    }
}
