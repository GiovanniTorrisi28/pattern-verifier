package com.patternverifier.verifiers;

import com.patternverifier.core.ClassMetadata;

import java.util.ArrayList;
import java.util.List;

// Strutturalmente identico a StrategyVerifier: Context + campo State + metodo di transizione.
// La distinzione tra State e Strategy è puramente semantica — questo è uno degli argomenti
// centrali della tesi a favore del conformance checking con dichiarazione esplicita del pattern.
public class StateVerifier {

    private final ClassMetadata context;
    private final ClassMetadata state;

    public StateVerifier(ClassMetadata context, ClassMetadata state) {
        this.context = context;
        this.state = state;
    }

    public List<String> verify() {
        List<String> violations = new ArrayList<>();
        checkStateIsAbstract(violations);
        checkStateHasMethods(violations);
        checkContextHasStateField(violations);
        checkContextHasTransitionMethod(violations);
        return violations;
    }

    private void checkStateIsAbstract(List<String> violations) {
        if (!state.isInterface() && !state.isAbstract()) {
            violations.add(state.getSimpleName()
                    + " non è né un'interfaccia né una classe astratta"
                    + " — lo State deve definire un contratto astratto per i comportamenti"
                    + " dipendenti dallo stato");
        }
    }

    private void checkStateHasMethods(List<String> violations) {
        boolean found = state.getMethods().stream()
                .anyMatch(m -> !m.isConstructor());
        if (!found) {
            violations.add(state.getSimpleName()
                    + " non ha metodi — lo State deve dichiarare almeno un metodo"
                    + " che rappresenta il comportamento dipendente dallo stato corrente");
        }
    }

    private void checkContextHasStateField(List<String> violations) {
        String stateName = state.getClassName();
        boolean found = context.getFields().stream()
                .anyMatch(f -> f.getTypeName().equals(stateName));
        if (!found) {
            violations.add(context.getSimpleName()
                    + " non ha un campo di tipo "
                    + state.getSimpleName()
                    + " — il Context deve mantenere un riferimento allo stato corrente");
        }
    }

    // Verifica OR: setter esplicito (setState*) oppure qualsiasi metodo che accetta
    // State come parametro (incluso il costruttore) — copre sia le transizioni
    // gestite dal Context che quelle gestite dai ConcreteState stessi.
    private void checkContextHasTransitionMethod(List<String> violations) {
        String stateName = state.getClassName();
        boolean found = context.getMethods().stream()
                .anyMatch(m -> m.getParameterTypeNames().contains(stateName));
        if (!found) {
            violations.add(context.getSimpleName()
                    + " non ha un metodo di transizione che accetti "
                    + state.getSimpleName()
                    + " come parametro — il Context deve permettere il cambiamento"
                    + " dello stato corrente (via setter, costruttore o metodo di transizione)");
        }
    }
}
