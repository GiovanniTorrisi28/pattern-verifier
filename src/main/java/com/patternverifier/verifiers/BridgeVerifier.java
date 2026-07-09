package com.patternverifier.verifiers;

import com.patternverifier.core.ClassMetadata;
import com.patternverifier.core.MethodInvocationAnalyzer;
import com.patternverifier.core.TypeHierarchy;

import java.util.ArrayList;
import java.util.List;

public class BridgeVerifier {

    private final ClassMetadata abstraction;
    private final ClassMetadata implementor;

    public BridgeVerifier(ClassMetadata abstraction, ClassMetadata implementor) {
        this.abstraction = abstraction;
        this.implementor = implementor;
    }

    public List<String> verify() {
        List<String> violations = new ArrayList<>();
        checkImplementorIsAbstract(violations);
        checkAbstractionHasImplementorField(violations);
        checkAbstractionDoesNotImplementImplementor(violations);
        checkAbstractionInvokesImplementorMethods(violations);
        return violations;
    }

    private void checkAbstractionInvokesImplementorMethods(List<String> violations) {
        if (!MethodInvocationAnalyzer.invokesMethodsOn(abstraction.getClassName(), implementor.getClassName())) {
            violations.add(abstraction.getSimpleName()
                    + " non invoca mai metodi sull'Implementor "
                    + implementor.getSimpleName()
                    + " — l'Abstraction deve delegare le operazioni all'Implementor (il \"ponte\" tra le due gerarchie)");
        }
    }

    private void checkImplementorIsAbstract(List<String> violations) {
        if (!implementor.isInterface() && !implementor.isAbstract()) {
            violations.add(implementor.getSimpleName()
                    + " non è né un'interfaccia né una classe astratta"
                    + " — l'Implementor del Bridge deve definire un contratto astratto"
                    + " per la gerarchia di implementazione");
        }
    }

    private void checkAbstractionHasImplementorField(List<String> violations) {
        String implementorName = implementor.getClassName();
        boolean found = abstraction.getFields().stream()
                .anyMatch(f -> f.getTypeName().equals(implementorName));
        if (!found) {
            violations.add(abstraction.getSimpleName()
                    + " non ha un campo di tipo "
                    + implementor.getSimpleName()
                    + " — l'Abstraction deve contenere un riferimento all'Implementor"
                    + " per delegargli le operazioni (il \"ponte\" tra le due gerarchie)");
        }
    }

    // Verifica che le due gerarchie siano indipendenti: Abstraction non deve
    // implementare né estendere Implementor, altrimenti le due gerarchie si sovrappongono
    // e il pattern perde il suo scopo di evoluzione indipendente.
    private void checkAbstractionDoesNotImplementImplementor(List<String> violations) {
        boolean sameHierarchy = TypeHierarchy.isAssignable(abstraction.getClassName(), implementor.getClassName());
        if (sameHierarchy) {
            violations.add(abstraction.getSimpleName()
                    + " implementa o estende "
                    + implementor.getSimpleName()
                    + " — nel Bridge le due gerarchie (Abstraction e Implementor)"
                    + " devono essere indipendenti e non sovrapposte");
        }
    }
}
