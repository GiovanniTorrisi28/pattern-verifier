package com.patternverifier.verifiers;

import com.patternverifier.core.ClassMetadata;
import com.patternverifier.core.MethodInvocationAnalyzer;

import java.util.ArrayList;
import java.util.List;

public class AdapterVerifier {

    private final ClassMetadata adapter;
    private final ClassMetadata adaptee;
    private final ClassMetadata target;

    public AdapterVerifier(ClassMetadata adapter, ClassMetadata adaptee, ClassMetadata target) {
        this.adapter = adapter;
        this.adaptee = adaptee;
        this.target = target;
    }

    public List<String> verify() {
        List<String> violations = new ArrayList<>();
        checkAdapterImplementsTarget(violations);
        checkAdapterHasAdapteeField(violations);
        checkAdapteeDoesNotImplementTarget(violations);
        checkAdapterDelegatesToAdaptee(violations);
        return violations;
    }

    private void checkAdapterDelegatesToAdaptee(List<String> violations) {
        if (!MethodInvocationAnalyzer.invokesMethodsOn(adapter.getClassName(), adaptee.getClassName())) {
            violations.add(adapter.getSimpleName()
                    + " non delega mai all'Adaptee "
                    + adaptee.getSimpleName()
                    + " — l'Adapter (object adapter) deve invocare metodi dell'Adaptee per realizzare la traduzione");
        }
    }

    private void checkAdapterImplementsTarget(List<String> violations) {
        boolean implements_ = adapter.getInterfaces().contains(target.getClassName());
        if (!implements_) {
            violations.add(
                adapter.getSimpleName() + " non implementa l'interfaccia " + target.getSimpleName() +
                " — l'Adapter deve implementare la stessa interfaccia del Target"
            );
        }
    }

    private void checkAdapterHasAdapteeField(List<String> violations) {
        boolean hasField = adapter.getFields().stream()
                .anyMatch(f -> f.getTypeName().equals(adaptee.getClassName()));
        if (!hasField) {
            violations.add(
                adapter.getSimpleName() + " non ha un campo di tipo " + adaptee.getSimpleName() +
                " — l'Adapter deve contenere un riferimento all'Adaptee (object adapter)"
            );
        }
    }

    private void checkAdapteeDoesNotImplementTarget(List<String> violations) {
        boolean alreadyImplements = adaptee.getInterfaces().contains(target.getClassName());
        if (alreadyImplements) {
            violations.add(
                adaptee.getSimpleName() + " implementa già " + target.getSimpleName() +
                " — se l'Adaptee implementa già il Target non è necessario un Adapter"
            );
        }
    }
}
