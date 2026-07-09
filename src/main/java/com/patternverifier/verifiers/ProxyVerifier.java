package com.patternverifier.verifiers;

import com.patternverifier.core.ClassMetadata;
import com.patternverifier.core.MethodInvocationAnalyzer;
import com.patternverifier.core.TypeHierarchy;

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
        checkProxyDelegatesToSubject(violations);
        return violations;
    }

    private void checkProxyDelegatesToSubject(List<String> violations) {
        boolean delegates = MethodInvocationAnalyzer.invokesMethodsOn(proxy.getClassName(), subject.getClassName())
                || MethodInvocationAnalyzer.invokesMethodsOn(proxy.getClassName(), realSubject.getClassName());
        if (!delegates) {
            violations.add(proxy.getSimpleName()
                    + " non delega mai al Subject "
                    + subject.getSimpleName()
                    + " — il Proxy deve invocare i metodi del Subject che controlla");
        }
    }

    private void checkProxyImplementsSubject(List<String> violations) {
        // Il Subject GoF può essere un'interfaccia o una classe astratta.
        if (!TypeHierarchy.isAssignable(proxy.getClassName(), subject.getClassName())) {
            violations.add(
                proxy.getSimpleName() + " non implementa né estende " + subject.getSimpleName() +
                " — il Proxy deve conformarsi allo stesso tipo (interfaccia o classe astratta) del Subject"
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
        if (!TypeHierarchy.isAssignable(realSubject.getClassName(), subject.getClassName())) {
            violations.add(
                realSubject.getSimpleName() + " non implementa né estende " + subject.getSimpleName() +
                " — il RealSubject deve conformarsi allo stesso tipo (interfaccia o classe astratta) del Subject"
            );
        }
    }
}
