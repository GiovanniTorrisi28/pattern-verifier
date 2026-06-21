package com.patternverifier.assertions;

import com.patternverifier.core.ClassAnalyzer;
import com.patternverifier.core.ClassMetadata;
import com.patternverifier.verifiers.ProxyVerifier;

import java.util.List;

public class ProxyAssert {

    private final Class<?> proxyClass;
    private final ClassMetadata proxyMetadata;
    private Class<?> realSubjectClass;

    public ProxyAssert(Class<?> proxyClass, ClassMetadata proxyMetadata) {
        this.proxyClass = proxyClass;
        this.proxyMetadata = proxyMetadata;
    }

    public ProxyAssert withRealSubject(Class<?> realSubject) {
        this.realSubjectClass = realSubject;
        return this;
    }

    /**
     * Specifica il Subject e scatta la verifica. Metodo terminale della catena.
     */
    public void forSubject(Class<?> subject) {
        ClassMetadata subjectMetadata    = ClassAnalyzer.analyze(subject);
        ClassMetadata realSubjectMetadata = ClassAnalyzer.analyze(realSubjectClass);

        List<String> violations = new ProxyVerifier(proxyMetadata, subjectMetadata, realSubjectMetadata).verify();
        if (!violations.isEmpty()) {
            throw new AssertionError(formatViolations(subject, violations));
        }
    }

    private String formatViolations(Class<?> subject, List<String> violations) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(proxyClass.getSimpleName())
          .append(": violazione pattern Proxy")
          .append(" (Subject=").append(subject.getSimpleName())
          .append(", RealSubject=").append(realSubjectClass.getSimpleName()).append(")\n");
        violations.forEach(v -> sb.append("  - ").append(v).append("\n"));
        return sb.toString();
    }
}
