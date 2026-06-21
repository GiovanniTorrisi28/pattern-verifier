package com.patternverifier.assertions;

import com.patternverifier.core.ClassAnalyzer;
import com.patternverifier.core.ClassMetadata;
import com.patternverifier.verifiers.BuilderVerifier;

import java.util.List;

public class BuilderAssert {

    private final Class<?> builderClass;
    private final ClassMetadata builderMetadata;

    public BuilderAssert(Class<?> clazz, ClassMetadata metadata) {
        this.builderClass = clazz;
        this.builderMetadata = metadata;
    }

    public void forProduct(Class<?> productClass) {
        ClassMetadata productMetadata = ClassAnalyzer.analyze(productClass);
        List<String> violations = new BuilderVerifier(builderMetadata, productMetadata).verify();
        if (!violations.isEmpty()) {
            throw new AssertionError(formatViolations(productClass, violations));
        }
    }

    private String formatViolations(Class<?> productClass, List<String> violations) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(builderClass.getSimpleName())
          .append(" / ").append(productClass.getSimpleName())
          .append(": violazione pattern Builder\n");
        violations.forEach(v -> sb.append("  - ").append(v).append("\n"));
        return sb.toString();
    }
}
