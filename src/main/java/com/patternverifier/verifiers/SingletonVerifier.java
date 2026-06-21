package com.patternverifier.verifiers;

import com.patternverifier.core.ClassMetadata;
import com.patternverifier.core.MethodInfo;

import java.util.ArrayList;
import java.util.List;

public class SingletonVerifier {

    private final ClassMetadata metadata;

    public SingletonVerifier(ClassMetadata metadata) {
        this.metadata = metadata;
    }

    /**
     * Esegue tutte le verifiche strutturali del Singleton e restituisce la lista
     * delle violazioni trovate. Lista vuota = implementazione corretta.
     */
    public List<String> verify() {
        List<String> violations = new ArrayList<>();
        checkPrivateConstructors(violations);
        checkStaticInstanceField(violations);
        checkStaticGetterMethod(violations);
        return violations;
    }

    private void checkPrivateConstructors(List<String> violations) {
        metadata.getMethods().stream()
                .filter(MethodInfo::isConstructor)
                .filter(m -> !m.isPrivate())
                .forEach(m -> violations.add(
                        "Il costruttore di " + metadata.getSimpleName() +
                        " non è private (modificatore attuale: " + accessLabel(m) + ")"
                ));
    }

    private void checkStaticInstanceField(List<String> violations) {
        String className = metadata.getClassName();
        boolean found = metadata.getFields().stream()
                .anyMatch(f -> f.isStatic() && f.getTypeName().equals(className));
        if (!found) {
            violations.add(
                    "Nessun campo static di tipo " + metadata.getSimpleName() +
                    " trovato — il Singleton deve mantenere l'istanza in un campo static"
            );
        }
    }

    private void checkStaticGetterMethod(List<String> violations) {
        String className = metadata.getClassName();
        boolean found = metadata.getMethods().stream()
                .anyMatch(m -> m.isStatic() && m.getReturnTypeName().equals(className));
        if (!found) {
            violations.add(
                    "Nessun metodo static che restituisce " + metadata.getSimpleName() +
                    " trovato — il Singleton deve esporre un metodo static di accesso all'istanza"
            );
        }
    }

    private String accessLabel(MethodInfo m) {
        if (m.isPublic()) return "public";
        return "package-private";
    }
}
