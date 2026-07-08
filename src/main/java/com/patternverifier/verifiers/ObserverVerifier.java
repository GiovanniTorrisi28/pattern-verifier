package com.patternverifier.verifiers;

import com.patternverifier.core.ClassMetadata;
import com.patternverifier.core.CollectionTypes;
import com.patternverifier.core.MethodInvocationAnalyzer;

import java.util.ArrayList;
import java.util.List;

public class ObserverVerifier {

    private final ClassMetadata subject;
    private final ClassMetadata observer;

    public ObserverVerifier(ClassMetadata subject, ClassMetadata observer) {
        this.subject = subject;
        this.observer = observer;
    }

    public List<String> verify() {
        List<String> violations = new ArrayList<>();
        checkObserverIsAbstract(violations);
        checkObserverHasUpdateMethod(violations);
        checkSubjectHasObserverCollection(violations);
        checkSubjectHasRegisterMethod(violations);
        checkSubjectHasNotifyMethod(violations);
        checkSubjectInvokesObserverMethods(violations);
        return violations;
    }

    private void checkSubjectInvokesObserverMethods(List<String> violations) {
        if (!MethodInvocationAnalyzer.invokesMethodsOn(subject.getClassName(), observer.getClassName())) {
            violations.add(subject.getSimpleName()
                    + " non invoca mai metodi sugli Observer di tipo "
                    + observer.getSimpleName()
                    + " — il Subject deve notificare gli observer chiamando i loro metodi di callback");
        }
    }

    private void checkObserverIsAbstract(List<String> violations) {
        if (!observer.isInterface() && !observer.isAbstract()) {
            violations.add(observer.getSimpleName()
                    + " non è né un'interfaccia né una classe astratta"
                    + " — l'Observer deve definire un contratto astratto per la notifica");
        }
    }

    // Naming convention GoF per il metodo di callback dell'Observer: update*, on*, handle*
    private void checkObserverHasUpdateMethod(List<String> violations) {
        boolean found = observer.getMethods().stream()
                .filter(m -> !m.isConstructor())
                .anyMatch(m -> m.getName().startsWith("update")
                        || m.getName().startsWith("on")
                        || m.getName().startsWith("handle"));
        if (!found) {
            violations.add(observer.getSimpleName()
                    + " non ha metodi con naming convention Observer (update*, on*, handle*)"
                    + " — l'Observer deve dichiarare un metodo di callback per la notifica");
        }
    }

    // Verifica che il campo Collection, quando dichiara i generics esplicitamente, contenga
    // davvero il tipo Observer — non una Collection qualunque (vedi CollectionTypes.isCollectionOf).
    private void checkSubjectHasObserverCollection(List<String> violations) {
        String observerName = observer.getClassName();
        boolean found = subject.getFields().stream()
                .anyMatch(f -> CollectionTypes.isCollectionOf(f, observerName));
        if (!found) {
            violations.add(subject.getSimpleName()
                    + " non ha un campo Collection di " + observer.getSimpleName()
                    + " per mantenere la lista degli observer"
                    + " — il Subject deve gestire una collezione di " + observer.getSimpleName());
        }
    }

    // Naming convention per la registrazione: add*, register*, subscribe* con Observer come parametro
    private void checkSubjectHasRegisterMethod(List<String> violations) {
        String observerName = observer.getClassName();
        boolean found = subject.getMethods().stream()
                .filter(m -> !m.isConstructor())
                .anyMatch(m -> (m.getName().startsWith("add")
                        || m.getName().startsWith("register")
                        || m.getName().startsWith("subscribe"))
                        && m.getParameterTypeNames().contains(observerName));
        if (!found) {
            violations.add(subject.getSimpleName()
                    + " non ha un metodo per registrare observer"
                    + " (add*/register*/subscribe* che accetti "
                    + observer.getSimpleName() + ")");
        }
    }

    // Naming convention per la notifica: notify*, fire*, dispatch*
    private void checkSubjectHasNotifyMethod(List<String> violations) {
        boolean found = subject.getMethods().stream()
                .filter(m -> !m.isConstructor())
                .anyMatch(m -> m.getName().startsWith("notify")
                        || m.getName().startsWith("fire")
                        || m.getName().startsWith("dispatch"));
        if (!found) {
            violations.add(subject.getSimpleName()
                    + " non ha un metodo di notifica (notify*/fire*/dispatch*)");
        }
    }
}
