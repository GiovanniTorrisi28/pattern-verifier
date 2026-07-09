package com.patternverifier.assertions;

import com.patternverifier.core.ClassAnalyzer;
import com.patternverifier.core.ClassMetadata;
import com.patternverifier.verifiers.ProxyVerifier;

import java.util.List;

public class ProxyAssert {

    private final Class<?> proxyClass;
    private final ClassMetadata proxyMetadata;
    private Class<?> realSubjectClass;
    private Class<?> subjectClass;

    public ProxyAssert(Class<?> proxyClass, ClassMetadata proxyMetadata) {
        this.proxyClass = proxyClass;
        this.proxyMetadata = proxyMetadata;
    }

    /**
     * Specifica il RealSubject. Scatena la verifica se il Subject è già stato dichiarato
     * (via {@link #forSubject}); altrimenti si limita a registrarlo — i due metodi sono
     * intercambiabili nell'ordine di chiamata, la verifica scatta a chiusura di qualunque
     * dei due che arrivi per secondo.
     */
    public ProxyAssert withRealSubject(Class<?> realSubject) {
        this.realSubjectClass = realSubject;
        if (subjectClass != null) {
            verify();
        }
        return this;
    }

    /**
     * Specifica il Subject. Scatena la verifica se il RealSubject è già stato dichiarato
     * (via {@link #withRealSubject}); altrimenti si limita a registrarlo — stesso schema
     * simmetrico di {@link #withRealSubject}.
     */
    public ProxyAssert forSubject(Class<?> subject) {
        this.subjectClass = subject;
        if (realSubjectClass != null) {
            verify();
        }
        return this;
    }

    private void verify() {
        ClassMetadata subjectMetadata = ClassAnalyzer.analyze(subjectClass);
        ClassMetadata realSubjectMetadata = ClassAnalyzer.analyze(realSubjectClass);

        List<String> violations = new ProxyVerifier(proxyMetadata, subjectMetadata, realSubjectMetadata).verify();
        if (!violations.isEmpty()) {
            throw new AssertionError(formatViolations(violations));
        }
    }

    private String formatViolations(List<String> violations) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(proxyClass.getSimpleName())
          .append(": violazione pattern Proxy")
          .append(" (Subject=").append(subjectClass.getSimpleName())
          .append(", RealSubject=").append(realSubjectClass.getSimpleName()).append(")\n");
        violations.forEach(v -> sb.append("  - ").append(v).append("\n"));
        return sb.toString();
    }
}
