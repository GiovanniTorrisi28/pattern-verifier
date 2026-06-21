package com.patternverifier.assertions;

import com.patternverifier.core.ClassAnalyzer;
import com.patternverifier.core.ClassMetadata;
import com.patternverifier.verifiers.VisitorVerifier;

import java.util.List;

public class VisitorAssert {

    private final Class<?> concreteVisitorClass;
    private final ClassMetadata concreteVisitorMetadata;
    private Class<?> visitorInterfaceClass;
    private ClassMetadata visitorInterfaceMetadata;

    public VisitorAssert(Class<?> clazz, ClassMetadata metadata) {
        this.concreteVisitorClass = clazz;
        this.concreteVisitorMetadata = metadata;
    }

    public VisitorAssert withVisitorInterface(Class<?> visitorClass) {
        this.visitorInterfaceClass = visitorClass;
        this.visitorInterfaceMetadata = ClassAnalyzer.analyze(visitorClass);
        return this;
    }

    public void withElement(Class<?> elementClass) {
        ClassMetadata elementMetadata = ClassAnalyzer.analyze(elementClass);
        List<String> violations = new VisitorVerifier(
                concreteVisitorMetadata, visitorInterfaceMetadata, elementMetadata).verify();
        if (!violations.isEmpty()) {
            throw new AssertionError(formatViolations(elementClass, violations));
        }
    }

    private String formatViolations(Class<?> elementClass, List<String> violations) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(concreteVisitorClass.getSimpleName())
          .append(" / ").append(visitorInterfaceClass.getSimpleName())
          .append(" / ").append(elementClass.getSimpleName())
          .append(": violazione pattern Visitor\n");
        violations.forEach(v -> sb.append("  - ").append(v).append("\n"));
        return sb.toString();
    }
}
