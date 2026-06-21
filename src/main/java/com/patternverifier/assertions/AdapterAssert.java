package com.patternverifier.assertions;

import com.patternverifier.core.ClassAnalyzer;
import com.patternverifier.core.ClassMetadata;
import com.patternverifier.verifiers.AdapterVerifier;

import java.util.List;

public class AdapterAssert {

    private final Class<?> adapterClass;
    private final ClassMetadata adapterMetadata;
    private Class<?> adapteeClass;

    public AdapterAssert(Class<?> adapterClass, ClassMetadata adapterMetadata) {
        this.adapterClass = adapterClass;
        this.adapterMetadata = adapterMetadata;
    }

    public AdapterAssert fromAdaptee(Class<?> adaptee) {
        this.adapteeClass = adaptee;
        return this;
    }

    /**
     * Specifica il Target e scatta la verifica.
     * È il metodo terminale della catena — esegue tutti i check e lancia AssertionError se trova violazioni.
     */
    public void toTarget(Class<?> target) {
        ClassMetadata adapteeMetadata = ClassAnalyzer.analyze(adapteeClass);
        ClassMetadata targetMetadata  = ClassAnalyzer.analyze(target);

        List<String> violations = new AdapterVerifier(adapterMetadata, adapteeMetadata, targetMetadata).verify();
        if (!violations.isEmpty()) {
            throw new AssertionError(formatViolations(target, violations));
        }
    }

    private String formatViolations(Class<?> target, List<String> violations) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(adapterClass.getSimpleName())
          .append(": violazione pattern Adapter")
          .append(" (Adaptee=").append(adapteeClass.getSimpleName())
          .append(", Target=").append(target.getSimpleName()).append(")\n");
        violations.forEach(v -> sb.append("  - ").append(v).append("\n"));
        return sb.toString();
    }
}
