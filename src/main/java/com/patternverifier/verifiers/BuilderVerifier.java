package com.patternverifier.verifiers;

import com.patternverifier.core.ClassMetadata;
import com.patternverifier.core.MethodInfo;
import com.patternverifier.core.SelfReturnAnalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BuilderVerifier {

    private final ClassMetadata builder;
    private final ClassMetadata product;

    public BuilderVerifier(ClassMetadata builder, ClassMetadata product) {
        this.builder = builder;
        this.product = product;
    }

    public List<String> verify() {
        List<String> violations = new ArrayList<>();
        checkHasFluentMethods(violations);
        checkHasBuildMethod(violations);
        return violations;
    }

    // Un metodo fluente restituisce il tipo del Builder stesso, permettendo la catena di chiamate.
    // Il solo tipo di ritorno dichiarato non basta: è soddisfatto anche da un metodo che
    // restituisce una nuova istanza invece di this (builder immutabile), comportamento diverso
    // dalla fluent interface canonica. Il controllo procede quindi in due passi — prima la firma
    // (Livello 1), poi il corpo via SelfReturnAnalyzer (istruzioni bytecode) — con messaggi
    // distinti, perché "nessun metodo fluente" e "metodi fluenti che non restituiscono this"
    // sono difetti diversi e richiedono correzioni diverse.
    private void checkHasFluentMethods(List<String> violations) {
        String builderName = builder.getClassName();
        List<String> declaredFluent = builder.getMethods().stream()
                .filter(m -> !m.isConstructor())
                .filter(m -> !isBuildMethod(m.getName()))
                .filter(m -> m.getReturnTypeName().equals(builderName))
                .map(MethodInfo::getName)
                .collect(Collectors.toList());

        if (declaredFluent.isEmpty()) {
            violations.add(builder.getSimpleName()
                    + " non ha metodi fluenti (nessun metodo restituisce "
                    + builder.getSimpleName()
                    + ") — il Builder deve permettere la catena di chiamate");
            return;
        }

        Set<String> selfReturning = SelfReturnAnalyzer.findSelfReturningMethods(builderName);
        boolean anyReturnsThis = declaredFluent.stream().anyMatch(selfReturning::contains);
        if (!anyReturnsThis) {
            violations.add(builder.getSimpleName()
                    + " dichiara metodi che restituiscono " + builder.getSimpleName()
                    + " (" + String.join(", ", declaredFluent) + ")"
                    + " ma nessuno di essi restituisce this"
                    + " — la catena di chiamate deve operare sulla stessa istanza,"
                    + " non su una nuova a ogni passo");
        }
    }

    // Naming convention GoF per il metodo terminale: build*, create*, construct*
    private void checkHasBuildMethod(List<String> violations) {
        String productName = product.getClassName();
        boolean found = builder.getMethods().stream()
                .filter(m -> !m.isConstructor())
                .filter(m -> isBuildMethod(m.getName()))
                .anyMatch(m -> m.getReturnTypeName().equals(productName));
        if (!found) {
            violations.add(builder.getSimpleName()
                    + " non ha un metodo build*/create*/construct* che restituisce "
                    + product.getSimpleName()
                    + " — il Builder deve avere un metodo terminale che costruisce il Product");
        }
    }

    private boolean isBuildMethod(String methodName) {
        return methodName.startsWith("build")
                || methodName.startsWith("create")
                || methodName.startsWith("construct");
    }
}
