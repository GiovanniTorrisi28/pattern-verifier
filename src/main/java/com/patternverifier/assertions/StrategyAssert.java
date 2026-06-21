package com.patternverifier.assertions;

import com.patternverifier.core.ClassAnalyzer;
import com.patternverifier.core.ClassMetadata;
import com.patternverifier.verifiers.StrategyVerifier;

import java.util.List;

public class StrategyAssert {

    private final Class<?> contextClass;
    private final ClassMetadata contextMetadata;

    public StrategyAssert(Class<?> clazz, ClassMetadata metadata) {
        this.contextClass = clazz;
        this.contextMetadata = metadata;
    }

    public void withStrategyInterface(Class<?> strategyClass) {
        ClassMetadata strategyMetadata = ClassAnalyzer.analyze(strategyClass);
        List<String> violations = new StrategyVerifier(contextMetadata, strategyMetadata).verify();
        if (!violations.isEmpty()) {
            throw new AssertionError(formatViolations(strategyClass, violations));
        }
    }

    private String formatViolations(Class<?> strategyClass, List<String> violations) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(contextClass.getSimpleName())
          .append(" / ").append(strategyClass.getSimpleName())
          .append(": violazione pattern Strategy\n");
        violations.forEach(v -> sb.append("  - ").append(v).append("\n"));
        return sb.toString();
    }
}
