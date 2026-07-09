package com.patternverifier.verifiers;

import com.patternverifier.core.ClassMetadata;
import com.patternverifier.core.MethodInvocationAnalyzer;
import com.patternverifier.core.TypeHierarchy;

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
        // Il Target GoF può essere un'interfaccia o una classe astratta: TypeHierarchy.isAssignable
        // copre entrambi i casi (interfacce e superclassi, a qualsiasi livello transitivo).
        if (!TypeHierarchy.isAssignable(adapter.getClassName(), target.getClassName())) {
            violations.add(
                adapter.getSimpleName() + " non implementa né estende " + target.getSimpleName() +
                " — l'Adapter deve conformarsi allo stesso tipo (interfaccia o classe astratta) del Target"
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
        // Copre anche il caso Target = classe astratta (prima si controllava solo l'interfaccia).
        boolean alreadyImplements = TypeHierarchy.isAssignable(adaptee.getClassName(), target.getClassName());
        if (alreadyImplements) {
            violations.add(
                adaptee.getSimpleName() + " implementa già " + target.getSimpleName() +
                " — se l'Adaptee implementa già il Target non è necessario un Adapter"
            );
        }
    }
}
