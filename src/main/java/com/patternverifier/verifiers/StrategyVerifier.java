package com.patternverifier.verifiers;

import com.patternverifier.core.ClassMetadata;
import com.patternverifier.core.InternalFactoryAssignmentAnalyzer;
import com.patternverifier.core.MethodInvocationAnalyzer;
import com.patternverifier.core.TypeHierarchy;

import java.util.ArrayList;
import java.util.List;

public class StrategyVerifier {

    private final ClassMetadata context;
    private final ClassMetadata strategy;

    public StrategyVerifier(ClassMetadata context, ClassMetadata strategy) {
        this.context = context;
        this.strategy = strategy;
    }

    public List<String> verify() {
        List<String> violations = new ArrayList<>();
        checkStrategyIsAbstract(violations);
        checkStrategyHasMethods(violations);
        checkContextHasStrategyField(violations);
        checkContextHasInjectionPoint(violations);
        checkContextInvokesStrategyMethods(violations);
        return violations;
    }

    private void checkStrategyIsAbstract(List<String> violations) {
        if (!strategy.isInterface() && !strategy.isAbstract()) {
            violations.add(strategy.getSimpleName()
                    + " non è né un'interfaccia né una classe astratta"
                    + " — la Strategy deve definire un contratto astratto per gli algoritmi");
        }
    }

    private void checkStrategyHasMethods(List<String> violations) {
        boolean hasMethods = strategy.getMethods().stream()
                .anyMatch(m -> !m.isConstructor());
        if (!hasMethods) {
            violations.add(strategy.getSimpleName()
                    + " non ha metodi — la Strategy deve dichiarare almeno un metodo"
                    + " che rappresenti l'algoritmo intercambiabile");
        }
    }

    private void checkContextHasStrategyField(List<String> violations) {
        if (context.isInterface()) {
            violations.add(context.getSimpleName()
                    + " è un'interfaccia — non può dichiarare un campo istanza di tipo "
                    + strategy.getSimpleName()
                    + ": il controllo sul campo non è applicabile a un'interfaccia,"
                    + " verificare le classi concrete che la implementano");
            return;
        }
        String strategyName = strategy.getClassName();
        boolean hasField = context.getFields().stream()
                .anyMatch(f -> TypeHierarchy.isAssignable(f.getTypeName(), strategyName));
        if (!hasField) {
            violations.add(context.getSimpleName()
                    + " non ha un campo di tipo "
                    + strategy.getSimpleName()
                    + " — il Context deve mantenere un riferimento alla Strategy corrente");
        }
    }

    private void checkContextInvokesStrategyMethods(List<String> violations) {
        if (!MethodInvocationAnalyzer.invokesMethodsOn(context.getClassName(), strategy.getClassName())) {
            violations.add(context.getSimpleName()
                    + " non invoca mai metodi su istanze di "
                    + strategy.getSimpleName()
                    + " — il Context deve delegare l'esecuzione dell'algoritmo alla Strategy");
        }
    }

    // Verifica OR a tre vie: (a) un setter/metodo che accetta la Strategy come parametro,
    // (b) un costruttore che la accetta, oppure (c) un metodo proprio della classe che
    // costruisce/ottiene una Strategy e la assegna a un proprio campo. GoF non richiede che
    // l'iniezione sia necessariamente esterna: un Context può decidere internamente quale
    // Strategy concreta usare (es. JHotDraw's SelectionTool sceglie tra più "tracker" — Tool
    // concreti — in base al punto cliccato, tramite metodi factory come createHandleTracker()
    // che restituiscono Tool, il cui risultato viene assegnato al campo fChild in mouseDown()).
    //
    // Il caso (c) è verificato da InternalFactoryAssignmentAnalyzer, che scansiona il bytecode
    // cercando una chiamata a un proprio metodo immediatamente seguita da un'assegnazione a un
    // proprio campo di tipo assegnabile alla Strategy — non basta che un metodo RESTITUISCA il
    // tipo giusto (quello lo fa anche un Factory Method puro, che consegna l'istanza a un
    // chiamante esterno senza mai conservarla): serve che il valore sia realmente conservato
    // come stato della classe. Distingue così i due casi strutturalmente identici scoperti in
    // JHotDraw: SelectionTool/ConnectionTool (assegnazione reale, genuini) da PolygonFigure,
    // EllipseFigure, PolyLineFigure, RoundRectangleFigure, AbstractFigure, DecoratorFigure
    // (connectorAt() costruisce un Connector nuovo ogni volta per il chiamante, mai assegnato
    // a un campo — è un Factory Method, non una Strategy interna).
    private void checkContextHasInjectionPoint(List<String> violations) {
        String strategyName = strategy.getClassName();
        boolean hasExternalInjection = context.getMethods().stream()
                .anyMatch(m -> m.getParameterTypeNames().stream()
                        .anyMatch(p -> TypeHierarchy.isAssignable(p, strategyName)));
        boolean hasInternalFactory = InternalFactoryAssignmentAnalyzer.storesInvocationResultInField(
                context.getClassName(), strategyName);
        if (!hasExternalInjection && !hasInternalFactory) {
            violations.add(context.getSimpleName()
                    + " non ha né un setter/costruttore che accetti "
                    + strategy.getSimpleName()
                    + " né un metodo interno che ne restituisca un'istanza"
                    + " — la Strategy deve essere iniettabile dall'esterno o selezionabile"
                    + " internamente per garantire l'intercambiabilità");
        }
    }
}
