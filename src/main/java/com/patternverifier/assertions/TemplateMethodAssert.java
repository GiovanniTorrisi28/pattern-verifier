package com.patternverifier.assertions;

import com.patternverifier.core.ClassMetadata;
import com.patternverifier.verifiers.TemplateMethodVerifier;

import java.util.List;

public class TemplateMethodAssert {

    private final Class<?> clazz;
    private final ClassMetadata metadata;

    public TemplateMethodAssert(Class<?> clazz, ClassMetadata metadata) {
        this.clazz = clazz;
        this.metadata = metadata;
    }

    public void withTemplateMethod(String methodName) {
        List<String> violations = new TemplateMethodVerifier(clazz, metadata, methodName).verify();
        if (!violations.isEmpty()) {
            throw new AssertionError(formatViolations(methodName, violations));
        }
    }

    private String formatViolations(String methodName, List<String> violations) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(clazz.getSimpleName())
          .append(".").append(methodName)
          .append(": violazione pattern Template Method\n");
        violations.forEach(v -> sb.append("  - ").append(v).append("\n"));
        return sb.toString();
    }
}
