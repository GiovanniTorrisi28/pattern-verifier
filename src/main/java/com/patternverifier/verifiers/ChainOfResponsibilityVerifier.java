package com.patternverifier.verifiers;

import com.patternverifier.core.ClassMetadata;

import java.util.ArrayList;
import java.util.List;

public class ChainOfResponsibilityVerifier {

    private final ClassMetadata handler;

    public ChainOfResponsibilityVerifier(ClassMetadata handler) {
        this.handler = handler;
    }

    public List<String> verify() {
        List<String> violations = new ArrayList<>();
        checkHandlerIsAbstract(violations);
        checkHandlerHasSelfReferenceField(violations);
        checkHandlerHasHandleMethod(violations);
        return violations;
    }

    private void checkHandlerIsAbstract(List<String> violations) {
        if (!handler.isInterface() && !handler.isAbstract()) {
            violations.add(handler.getSimpleName()
                    + " non è né un'interfaccia né una classe astratta"
                    + " — il Handler del Chain of Responsibility deve definire"
                    + " un contratto astratto per la gestione delle richieste");
        }
    }

    // La self-reference (campo del proprio stesso tipo) è la proprietà strutturale
    // esclusiva del Chain of Responsibility tra tutti i pattern GoF canonici.
    // Rappresenta il "successore" nella catena a cui passare la richiesta se
    // il handler corrente non la gestisce.
    private void checkHandlerHasSelfReferenceField(List<String> violations) {
        String handlerName = handler.getClassName();
        boolean found = handler.getFields().stream()
                .anyMatch(f -> f.getTypeName().equals(handlerName));
        if (!found) {
            violations.add(handler.getSimpleName()
                    + " non ha un campo del proprio stesso tipo"
                    + " — il Handler deve contenere un riferimento al successore"
                    + " nella catena (self-reference: campo di tipo "
                    + handler.getSimpleName() + ")");
        }
    }

    private void checkHandlerHasHandleMethod(List<String> violations) {
        boolean found = handler.getMethods().stream()
                .filter(m -> !m.isConstructor())
                .anyMatch(m -> m.getName().startsWith("handle")
                        || m.getName().startsWith("process")
                        || m.getName().startsWith("canHandle")
                        || m.getName().startsWith("execute"));
        if (!found) {
            violations.add(handler.getSimpleName()
                    + " non ha un metodo di gestione con naming convention"
                    + " Chain of Responsibility (handle*, process*, canHandle*, execute*)");
        }
    }
}
