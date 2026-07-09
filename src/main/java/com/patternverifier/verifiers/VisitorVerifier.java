package com.patternverifier.verifiers;

import com.patternverifier.core.ClassMetadata;
import com.patternverifier.core.MethodInvocationAnalyzer;
import com.patternverifier.core.TypeHierarchy;

import java.util.ArrayList;
import java.util.List;

public class VisitorVerifier {

    private final ClassMetadata concreteVisitor;
    private final ClassMetadata visitorInterface;
    private final ClassMetadata element;

    public VisitorVerifier(ClassMetadata concreteVisitor, ClassMetadata visitorInterface,
                            ClassMetadata element) {
        this.concreteVisitor = concreteVisitor;
        this.visitorInterface = visitorInterface;
        this.element = element;
    }

    public List<String> verify() {
        List<String> violations = new ArrayList<>();
        checkVisitorIsAbstract(violations);
        checkVisitorHasVisitMethod(violations);
        checkElementHasAcceptMethod(violations);
        checkConcreteVisitorImplementsVisitor(violations);
        checkConcreteVisitorHasConcreteVisitMethod(violations);
        checkElementCallsVisitorMethods(violations);
        return violations;
    }

    private void checkElementCallsVisitorMethods(List<String> violations) {
        // Se Element è astratto o interfaccia, il corpo di accept() è nelle ConcreteElement
        // che non sono passate al verifier — skip per evitare falsi positivi
        if (element.isAbstract() || element.isInterface()) return;
        if (!MethodInvocationAnalyzer.invokesMethodsOn(element.getClassName(), visitorInterface.getClassName())) {
            violations.add(element.getSimpleName()
                    + " non invoca metodi sul Visitor nel metodo accept"
                    + " — il double dispatch richiede che accept chiami visitor.visit(this)");
        }
    }

    private void checkVisitorIsAbstract(List<String> violations) {
        if (!visitorInterface.isInterface() && !visitorInterface.isAbstract()) {
            violations.add(visitorInterface.getSimpleName()
                    + " non è né un'interfaccia né una classe astratta"
                    + " — il Visitor deve definire un contratto astratto"
                    + " per le operazioni sugli elementi");
        }
    }

    private void checkVisitorHasVisitMethod(List<String> violations) {
        boolean found = visitorInterface.getMethods().stream()
                .filter(m -> !m.isConstructor())
                .anyMatch(m -> m.getName().startsWith("visit"));
        if (!found) {
            violations.add(visitorInterface.getSimpleName()
                    + " non ha metodi con naming convention Visitor (visit*)"
                    + " — il Visitor deve dichiarare almeno un metodo visit*"
                    + " per ciascun tipo di elemento da visitare");
        }
    }

    // Verifica che Element abbia un metodo accept che accetta il Visitor come parametro.
    // accept(Visitor) è il meccanismo del double dispatch: chiama visitor.visit(this)
    // per delegare l'operazione al Visitor concreto corretto.
    private void checkElementHasAcceptMethod(List<String> violations) {
        String visitorName = visitorInterface.getClassName();
        boolean found = element.getMethods().stream()
                .filter(m -> !m.isConstructor())
                .anyMatch(m -> m.getName().equals("accept")
                        && m.getParameterTypeNames().contains(visitorName));
        if (!found) {
            violations.add(element.getSimpleName()
                    + " non ha un metodo accept("
                    + visitorInterface.getSimpleName()
                    + ") — l'Element deve dichiarare accept per abilitare il double dispatch");
        }
    }

    private void checkConcreteVisitorImplementsVisitor(List<String> violations) {
        if (!TypeHierarchy.isAssignable(concreteVisitor.getClassName(), visitorInterface.getClassName())) {
            violations.add(concreteVisitor.getSimpleName()
                    + " non implementa né estende "
                    + visitorInterface.getSimpleName()
                    + " — il ConcreteVisitor deve implementare l'interfaccia Visitor");
        }
    }

    private void checkConcreteVisitorHasConcreteVisitMethod(List<String> violations) {
        boolean found = concreteVisitor.getMethods().stream()
                .filter(m -> !m.isConstructor())
                .anyMatch(m -> m.getName().startsWith("visit") && !m.isAbstract());
        if (!found) {
            violations.add(concreteVisitor.getSimpleName()
                    + " non ha un'implementazione concreta di un metodo visit*"
                    + " — il ConcreteVisitor deve fornire l'operazione specifica"
                    + " per almeno un tipo di elemento");
        }
    }
}
