package com.patternverifier.verifiers;

import com.patternverifier.core.ClassMetadata;

import java.util.ArrayList;
import java.util.List;

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
    // Si verifica staticamente sul tipo di ritorno dichiarato — non serve analizzare il corpo.
    private void checkHasFluentMethods(List<String> violations) {
        String builderName = builder.getClassName();
        boolean found = builder.getMethods().stream()
                .filter(m -> !m.isConstructor())
                .filter(m -> !isBuildMethod(m.getName()))
                .anyMatch(m -> m.getReturnTypeName().equals(builderName));
        if (!found) {
            violations.add(builder.getSimpleName()
                    + " non ha metodi fluenti (nessun metodo restituisce "
                    + builder.getSimpleName()
                    + ") — il Builder deve permettere la catena di chiamate");
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
