package com.patternverifier.verifiers;

import com.patternverifier.core.ClassMetadata;
import com.patternverifier.core.MethodInvocationAnalyzer;

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
                .anyMatch(f -> f.getTypeName().equals(strategyName));
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

    // Verifica OR: basta che esista un metodo (setter) O un costruttore che accetti Strategy.
    // Entrambe sono forme valide di dependency injection della Strategy nel Context.
    private void checkContextHasInjectionPoint(List<String> violations) {
        String strategyName = strategy.getClassName();
        boolean hasInjection = context.getMethods().stream()
                .anyMatch(m -> m.getParameterTypeNames().contains(strategyName));
        if (!hasInjection) {
            violations.add(context.getSimpleName()
                    + " non ha né un setter né un costruttore che accetti "
                    + strategy.getSimpleName()
                    + " — la Strategy deve essere iniettabile per garantire l'intercambiabilità");
        }
    }
}
