package com.patternverifier.assertions;

import com.patternverifier.core.ClassAnalyzer;
import com.patternverifier.core.ClassMetadata;
import com.patternverifier.verifiers.FactoryMethodVerifier;

import java.util.List;

public class FactoryMethodAssert {

    private final Class<?> creatorClass;
    private final ClassMetadata creatorMetadata;
    private String factoryMethodName;
    private Class<?> productClass;

    public FactoryMethodAssert(Class<?> creatorClass, ClassMetadata creatorMetadata) {
        this.creatorClass = creatorClass;
        this.creatorMetadata = creatorMetadata;
    }

    public FactoryMethodAssert withAbstractFactoryMethod(String methodName, Class<?> product) {
        this.factoryMethodName = methodName;
        this.productClass = product;
        return this;
    }

    /**
     * Specifica il ConcreteCreator e scatta la verifica. Metodo terminale della catena.
     */
    public void withConcreteCreator(Class<?> concreteCreator) {
        ClassMetadata concreteCreatorMetadata = ClassAnalyzer.analyze(concreteCreator);
        ClassMetadata productMetadata         = ClassAnalyzer.analyze(productClass);

        List<String> violations = new FactoryMethodVerifier(
                creatorMetadata, concreteCreatorMetadata, productMetadata, factoryMethodName
        ).verify();

        if (!violations.isEmpty()) {
            throw new AssertionError(formatViolations(concreteCreator, violations));
        }
    }

    private String formatViolations(Class<?> concreteCreator, List<String> violations) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(creatorClass.getSimpleName())
          .append(": violazione pattern Factory Method")
          .append(" (ConcreteCreator=").append(concreteCreator.getSimpleName())
          .append(", Product=").append(productClass.getSimpleName()).append(")\n");
        violations.forEach(v -> sb.append("  - ").append(v).append("\n"));
        return sb.toString();
    }
}
