package com.patternverifier.assertions;

import com.patternverifier.core.ClassAnalyzer;
import com.patternverifier.core.ClassMetadata;
import com.patternverifier.verifiers.AbstractFactoryVerifier;

import java.util.Arrays;
import java.util.List;

public class AbstractFactoryAssert {

    private final Class<?> abstractFactoryClass;
    private final ClassMetadata abstractFactoryMetadata;
    private List<Class<?>> products = List.of();

    public AbstractFactoryAssert(Class<?> clazz, ClassMetadata metadata) {
        this.abstractFactoryClass = clazz;
        this.abstractFactoryMetadata = metadata;
    }

    public AbstractFactoryAssert producing(Class<?>... products) {
        this.products = Arrays.asList(products);
        return this;
    }

    public void withConcreteFactory(Class<?> concreteFactory) {
        ClassMetadata concreteMetadata = ClassAnalyzer.analyze(concreteFactory);
        List<String> violations = new AbstractFactoryVerifier(
                abstractFactoryMetadata, products, concreteMetadata
        ).verify();
        if (!violations.isEmpty()) {
            throw new AssertionError(formatViolations(concreteFactory, violations));
        }
    }

    private String formatViolations(Class<?> concreteFactory, List<String> violations) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(abstractFactoryClass.getSimpleName())
          .append(" / ").append(concreteFactory.getSimpleName())
          .append(": violazione pattern Abstract Factory\n");
        violations.forEach(v -> sb.append("  - ").append(v).append("\n"));
        return sb.toString();
    }
}
