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
    private Class<?> elementClass;
    private ClassMetadata elementMetadata;

    public VisitorAssert(Class<?> clazz, ClassMetadata metadata) {
        this.concreteVisitorClass = clazz;
        this.concreteVisitorMetadata = metadata;
    }

    public VisitorAssert withVisitorInterface(Class<?> visitorClass) {
        this.visitorInterfaceClass = visitorClass;
        this.visitorInterfaceMetadata = ClassAnalyzer.analyze(visitorClass);
        return this;
    }

    /**
     * Specifica l'Element visitato. Non scatena la verifica: il double dispatch
     * (accept che chiama visitor.visit(this)) vive nel corpo di accept, che esiste solo in una
     * classe concreta. Poiché nel caso canonico l'Element è un'interfaccia, verificare la sola
     * Element lascerebbe la proprietà comportamentale centrale del Visitor mai controllata.
     * La catena richiede quindi di chiudersi esplicitamente con {@link #withConcreteElement}
     * oppure, dichiarandolo come scelta, con {@link #withoutConcreteElement}.
     */
    public VisitorAssert withElement(Class<?> elementClass) {
        this.elementClass = elementClass;
        this.elementMetadata = ClassAnalyzer.analyze(elementClass);
        return this;
    }

    /**
     * Specifica la ConcreteElement che implementa accept e scatta la verifica, inclusi il
     * controllo che essa si conformi all'Element e il double dispatch nel corpo di accept.
     * Metodo terminale della catena.
     */
    public void withConcreteElement(Class<?> concreteElementClass) {
        requireElementDeclared();
        ClassMetadata concreteElementMetadata = ClassAnalyzer.analyze(concreteElementClass);
        List<String> violations = new VisitorVerifier(
                concreteVisitorMetadata, visitorInterfaceMetadata,
                elementMetadata, concreteElementMetadata).verify();
        if (!violations.isEmpty()) {
            throw new AssertionError(formatViolations(concreteElementClass.getSimpleName(), violations));
        }
    }

    /**
     * Scatta la verifica senza dichiarare una ConcreteElement: il double dispatch viene
     * controllato solo se l'Element stessa è una classe concreta, altrimenti resta non
     * verificato. A differenza di omettere un metodo terminale, questa è una dichiarazione
     * intenzionale — chi legge il test vede che la rinuncia è una scelta, non una dimenticanza.
     * Metodo terminale della catena.
     */
    public void withoutConcreteElement() {
        requireElementDeclared();
        List<String> violations = new VisitorVerifier(
                concreteVisitorMetadata, visitorInterfaceMetadata, elementMetadata).verify();
        if (!violations.isEmpty()) {
            throw new AssertionError(formatViolations(elementClass.getSimpleName(), violations));
        }
    }

    private void requireElementDeclared() {
        if (elementMetadata == null) {
            throw new IllegalStateException(
                    "Chiamare withElement() prima di withConcreteElement()/withoutConcreteElement()");
        }
    }

    private String formatViolations(String lastRoleSimpleName, List<String> violations) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(concreteVisitorClass.getSimpleName())
          .append(" / ").append(visitorInterfaceClass.getSimpleName())
          .append(" / ").append(lastRoleSimpleName)
          .append(": violazione pattern Visitor\n");
        violations.forEach(v -> sb.append("  - ").append(v).append("\n"));
        return sb.toString();
    }
}
