package com.patternverifier.verifiers;

import com.patternverifier.core.ClassMetadata;
import com.patternverifier.core.InternalFactoryAssignmentAnalyzer;
import com.patternverifier.core.MethodInvocationAnalyzer;
import com.patternverifier.core.TypeHierarchy;

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
        checkContextInvokesStateMethods(violations);
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
        if (context.isInterface()) {
            violations.add(context.getSimpleName()
                    + " è un'interfaccia — non può dichiarare un campo istanza di tipo "
                    + state.getSimpleName()
                    + ": il controllo sul campo non è applicabile a un'interfaccia,"
                    + " verificare le classi concrete che la implementano");
            return;
        }
        String stateName = state.getClassName();
        boolean found = context.getFields().stream()
                .anyMatch(f -> TypeHierarchy.isAssignable(f.getTypeName(), stateName));
        if (!found) {
            violations.add(context.getSimpleName()
                    + " non ha un campo di tipo "
                    + state.getSimpleName()
                    + " — il Context deve mantenere un riferimento allo stato corrente");
        }
    }

    private void checkContextInvokesStateMethods(List<String> violations) {
        if (!MethodInvocationAnalyzer.invokesMethodsOn(context.getClassName(), state.getClassName())) {
            violations.add(context.getSimpleName()
                    + " non invoca mai metodi su istanze di "
                    + state.getSimpleName()
                    + " — il Context deve delegare il comportamento allo State corrente");
        }
    }

    // Verifica OR a tre vie: (a) setter esplicito (setState*) o qualsiasi metodo che accetta
    // State come parametro (incluso il costruttore) — transizione esterna; (b) un metodo
    // proprio della classe che costruisce/ottiene uno State e lo assegna a un proprio campo —
    // transizione decisa dal Context stesso. Il testo GoF ammette esplicitamente che sia il
    // Context a decidere quale stato succeda a quale, non solo l'iniezione esterna: es.
    // JHotDraw's SelectionTool sceglie tra più "tracker" (Tool concreti) in base al punto
    // cliccato, tramite metodi factory come createHandleTracker() che restituiscono Tool, il
    // cui risultato viene assegnato al campo fChild in mouseDown().
    //
    // Il caso (b) è verificato da InternalFactoryAssignmentAnalyzer (bytecode: chiamata a un
    // proprio metodo immediatamente seguita da un'assegnazione a un proprio campo di tipo
    // assegnabile allo State) — stesso identico controllo di StrategyVerifier.
    // checkContextHasInjectionPoint, e stesso motivo: non basta che un metodo RESTITUISCA il
    // tipo giusto (lo fa anche un Factory Method puro, che consegna l'istanza a un chiamante
    // esterno senza conservarla), serve che il valore sia realmente assegnato come stato della
    // classe. Distingue SelectionTool (fChild = createHandleTracker(...), genuino) da
    // StandardDrawingView (tool() è un puro forwarder verso fEditor.tool(), mai un'assegnazione
    // a un proprio campo — resta correttamente non conforme).
    private void checkContextHasTransitionMethod(List<String> violations) {
        String stateName = state.getClassName();
        boolean hasExternalTransition = context.getMethods().stream()
                .anyMatch(m -> m.getParameterTypeNames().stream()
                        .anyMatch(p -> TypeHierarchy.isAssignable(p, stateName)));
        boolean hasInternalFactory = InternalFactoryAssignmentAnalyzer.storesInvocationResultInField(
                context.getClassName(), stateName);
        if (!hasExternalTransition && !hasInternalFactory) {
            violations.add(context.getSimpleName()
                    + " non ha un metodo di transizione che accetti "
                    + state.getSimpleName()
                    + " come parametro né un metodo interno che ne restituisca un'istanza"
                    + " — il Context deve permettere il cambiamento dello stato corrente"
                    + " (via setter, costruttore, o selezione interna tra più stati concreti)");
        }
    }
}
