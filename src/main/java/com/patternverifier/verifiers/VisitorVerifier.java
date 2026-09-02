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
    // Opzionale (null se non dichiarato): la ConcreteElement che implementa davvero accept().
    private final ClassMetadata concreteElement;

    public VisitorVerifier(ClassMetadata concreteVisitor, ClassMetadata visitorInterface,
                            ClassMetadata element) {
        this(concreteVisitor, visitorInterface, element, null);
    }

    public VisitorVerifier(ClassMetadata concreteVisitor, ClassMetadata visitorInterface,
                            ClassMetadata element, ClassMetadata concreteElement) {
        this.concreteVisitor = concreteVisitor;
        this.visitorInterface = visitorInterface;
        this.element = element;
        this.concreteElement = concreteElement;
    }

    public List<String> verify() {
        List<String> violations = new ArrayList<>();
        checkVisitorIsAbstract(violations);
        checkVisitorHasVisitMethod(violations);
        checkElementHasAcceptMethod(violations);
        checkConcreteVisitorImplementsVisitor(violations);
        checkConcreteVisitorHasConcreteVisitMethod(violations);
        if (concreteElement != null) {
            checkConcreteElementImplementsElement(violations);
            checkConcreteElementHasConcreteAccept(violations);
        }
        checkDoubleDispatch(violations);
        return violations;
    }

    // Il double dispatch (accept che chiama visitor.visit(this)) vive nel corpo di accept(),
    // che esiste solo in una classe concreta. Se il programmatore ha dichiarato una
    // ConcreteElement la verifica si esegue su quella — il caso canonico, in cui Element è
    // un'interfaccia e il corpo reale sta nelle sue implementazioni. Se non l'ha dichiarata si
    // ricade sul comportamento storico: si verifica Element stessa quando è concreta, si salta
    // quando è astratta o interfaccia (per non produrre falsi positivi su un corpo assente).
    private void checkDoubleDispatch(List<String> violations) {
        ClassMetadata target = (concreteElement != null) ? concreteElement : element;
        if (target.isAbstract() || target.isInterface()) return;
        if (!MethodInvocationAnalyzer.invokesMethodsOn(target.getClassName(), visitorInterface.getClassName())) {
            violations.add(target.getSimpleName()
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

    private void checkConcreteElementImplementsElement(List<String> violations) {
        if (!TypeHierarchy.isAssignable(concreteElement.getClassName(), element.getClassName())) {
            violations.add(concreteElement.getSimpleName()
                    + " non implementa né estende "
                    + element.getSimpleName()
                    + " — il ConcreteElement deve conformarsi al tipo Element visitato");
        }
    }

    private void checkConcreteElementHasConcreteAccept(List<String> violations) {
        String visitorName = visitorInterface.getClassName();
        boolean found = concreteElement.getMethods().stream()
                .filter(m -> !m.isConstructor())
                .anyMatch(m -> m.getName().equals("accept")
                        && !m.isAbstract()
                        && m.getParameterTypeNames().contains(visitorName));
        if (!found) {
            violations.add(concreteElement.getSimpleName()
                    + " non ha un'implementazione concreta di accept("
                    + visitorInterface.getSimpleName()
                    + ") — il ConcreteElement deve implementare accept per realizzare il double dispatch");
        }
    }
}
