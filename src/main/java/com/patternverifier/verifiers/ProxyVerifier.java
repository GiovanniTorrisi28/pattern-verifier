package com.patternverifier.verifiers;

import com.patternverifier.core.ClassMetadata;

import java.util.ArrayList;
import java.util.List;

public class ProxyVerifier {

    private final ClassMetadata proxy;
    private final ClassMetadata subject;
    private final ClassMetadata realSubject;

    public ProxyVerifier(ClassMetadata proxy, ClassMetadata subject, ClassMetadata realSubject) {
        this.proxy = proxy;
        this.subject = subject;
        this.realSubject = realSubject;
    }

    public List<String> verify() {
        List<String> violations = new ArrayList<>();
        checkProxyImplementsSubject(violations);
        checkProxyHasSubjectField(violations);
        checkRealSubjectImplementsSubject(violations);
        return violations;
    }

    private void checkProxyImplementsSubject(List<String> violations) {
        if (!proxy.getInterfaces().contains(subject.getClassName())) {
            violations.add(
                proxy.getSimpleName() + " non implementa l'interfaccia " + subject.getSimpleName() +
                " — il Proxy deve implementare la stessa interfaccia del Subject"
            );
        }
    }

    private void checkProxyHasSubjectField(List<String> violations) {
        // Il campo può essere del tipo dell'interfaccia Subject o del tipo concreto RealSubject
        boolean hasField = proxy.getFields().stream()
                .anyMatch(f -> f.getTypeName().equals(subject.getClassName())
                            || f.getTypeName().equals(realSubject.getClassName()));
        if (!hasField) {
            violations.add(
                proxy.getSimpleName() + " non ha un campo di tipo " +
                subject.getSimpleName() + " o " + realSubject.getSimpleName() +
                " — il Proxy deve mantenere un riferimento al Subject"
            );
        }
    }

    private void checkRealSubjectImplementsSubject(List<String> violations) {
        if (!realSubject.getInterfaces().contains(subject.getClassName())) {
            violations.add(
                realSubject.getSimpleName() + " non implementa " + subject.getSimpleName() +
                " — il RealSubject deve implementare la stessa interfaccia del Proxy"
            );
        }
    }
}
