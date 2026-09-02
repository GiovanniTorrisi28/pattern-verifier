package com.patternverifier.assertions;

import com.patternverifier.core.ClassAnalyzer;
import com.patternverifier.core.ClassMetadata;
import com.patternverifier.verifiers.MediatorVerifier;

import java.util.ArrayList;
import java.util.List;

public class MediatorAssert {

    private final Class<?> concreteMediatorClass;
    private final ClassMetadata concreteMediatorMetadata;
    private Class<?> mediatorClass;
    private ClassMetadata mediatorMetadata;

    public MediatorAssert(Class<?> clazz, ClassMetadata metadata) {
        this.concreteMediatorClass = clazz;
        this.concreteMediatorMetadata = metadata;
    }

    public MediatorAssert withMediatorInterface(Class<?> mediatorClass) {
        this.mediatorClass = mediatorClass;
        this.mediatorMetadata = ClassAnalyzer.analyze(mediatorClass);
        return this;
    }

    /**
     * Dichiara i Colleague coordinati dal Mediator e scatta la verifica. L'enumerazione esplicita
     * è ciò che rende verificabile la proprietà negativa del pattern — che nessun Colleague
     * riferisca direttamente un altro Colleague — su un insieme finito e noto. Metodo terminale.
     */
    public void withColleagues(Class<?>... colleagueClasses) {
        if (mediatorMetadata == null) {
            throw new IllegalStateException("Chiamare withMediatorInterface() prima di withColleagues()");
        }
        List<ClassMetadata> colleagues = new ArrayList<>();
        for (Class<?> colleague : colleagueClasses) {
            colleagues.add(ClassAnalyzer.analyze(colleague));
        }
        List<String> violations =
                new MediatorVerifier(concreteMediatorMetadata, mediatorMetadata, colleagues).verify();
        if (!violations.isEmpty()) {
            throw new AssertionError(formatViolations(violations));
        }
    }

    private String formatViolations(List<String> violations) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(concreteMediatorClass.getSimpleName())
          .append(" / ").append(mediatorClass.getSimpleName())
          .append(": violazione pattern Mediator\n");
        violations.forEach(v -> sb.append("  - ").append(v).append("\n"));
        return sb.toString();
    }
}
