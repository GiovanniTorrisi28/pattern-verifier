package com.patternverifier.assertions;

import com.patternverifier.core.ClassAnalyzer;
import com.patternverifier.core.ClassMetadata;
import com.patternverifier.verifiers.PrototypeVerifier;

import java.util.List;

public class PrototypeAssert {

    private final Class<?> concretePrototypeClass;
    private final ClassMetadata concretePrototypeMetadata;
    private Class<?> prototypeClass;
    private ClassMetadata prototypeMetadata;

    public PrototypeAssert(Class<?> clazz, ClassMetadata metadata) {
        this.concretePrototypeClass = clazz;
        this.concretePrototypeMetadata = metadata;
    }

    /**
     * Specifica il Prototype. Non scatena la verifica: la proprietà che distingue il Prototype
     * da una qualunque classe clonabile è che il <b>Client</b> crei le istanze clonando invece
     * di usare {@code new}, e quel controllo richiede di dichiarare il Client. La catena deve
     * quindi chiudersi con {@link #withClient} oppure, come scelta esplicita, con
     * {@link #withoutClient}.
     */
    public PrototypeAssert withPrototype(Class<?> prototypeClass) {
        this.prototypeClass = prototypeClass;
        this.prototypeMetadata = ClassAnalyzer.analyze(prototypeClass);
        return this;
    }

    /**
     * Specifica il Client e scatta la verifica, incluso il controllo che detenga il prototipo e
     * ne invochi la clonazione. Metodo terminale della catena.
     */
    public void withClient(Class<?> clientClass) {
        requirePrototypeDeclared();
        ClassMetadata clientMetadata = ClassAnalyzer.analyze(clientClass);
        List<String> violations = new PrototypeVerifier(
                concretePrototypeMetadata, prototypeMetadata, clientMetadata).verify();
        if (!violations.isEmpty()) {
            throw new AssertionError(formatViolations(clientClass.getSimpleName(), violations));
        }
    }

    /**
     * Scatta la verifica senza controllare il Client: si verificano solo il contratto di
     * clonazione del Prototype e la sua realizzazione nel ConcretePrototype. Metodo terminale.
     */
    public void withoutClient() {
        requirePrototypeDeclared();
        List<String> violations = new PrototypeVerifier(
                concretePrototypeMetadata, prototypeMetadata).verify();
        if (!violations.isEmpty()) {
            throw new AssertionError(formatViolations(prototypeClass.getSimpleName(), violations));
        }
    }

    private void requirePrototypeDeclared() {
        if (prototypeMetadata == null) {
            throw new IllegalStateException(
                    "Chiamare withPrototype() prima di withClient()/withoutClient()");
        }
    }

    private String formatViolations(String lastRoleSimpleName, List<String> violations) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(concretePrototypeClass.getSimpleName())
          .append(" / ").append(lastRoleSimpleName)
          .append(": violazione pattern Prototype\n");
        violations.forEach(v -> sb.append("  - ").append(v).append("\n"));
        return sb.toString();
    }
}
