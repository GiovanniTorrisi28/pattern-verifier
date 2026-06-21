package com.patternverifier.assertions;

import com.patternverifier.core.ClassAnalyzer;
import com.patternverifier.core.ClassMetadata;
import com.patternverifier.verifiers.ObserverVerifier;

import java.util.List;

public class ObserverAssert {

    private final Class<?> subjectClass;
    private final ClassMetadata subjectMetadata;

    public ObserverAssert(Class<?> clazz, ClassMetadata metadata) {
        this.subjectClass = clazz;
        this.subjectMetadata = metadata;
    }

    public void withObserverInterface(Class<?> observerClass) {
        ClassMetadata observerMetadata = ClassAnalyzer.analyze(observerClass);
        List<String> violations = new ObserverVerifier(subjectMetadata, observerMetadata).verify();
        if (!violations.isEmpty()) {
            throw new AssertionError(formatViolations(observerClass, violations));
        }
    }

    private String formatViolations(Class<?> observerClass, List<String> violations) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(subjectClass.getSimpleName())
          .append(" / ").append(observerClass.getSimpleName())
          .append(": violazione pattern Observer\n");
        violations.forEach(v -> sb.append("  - ").append(v).append("\n"));
        return sb.toString();
    }
}
