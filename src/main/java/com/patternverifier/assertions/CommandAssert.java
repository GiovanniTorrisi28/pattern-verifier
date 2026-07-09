package com.patternverifier.assertions;

import com.patternverifier.core.ClassAnalyzer;
import com.patternverifier.core.ClassMetadata;
import com.patternverifier.verifiers.CommandVerifier;

import java.util.List;

public class CommandAssert {

    private final Class<?> concreteCommandClass;
    private final ClassMetadata concreteCommandMetadata;
    private Class<?> commandClass;
    private ClassMetadata commandMetadata;

    public CommandAssert(Class<?> clazz, ClassMetadata metadata) {
        this.concreteCommandClass = clazz;
        this.concreteCommandMetadata = metadata;
    }

    /**
     * Specifica il Command. Non scatena la verifica: la delega al Receiver è la proprietà
     * centrale del Command GoF (senza di essa una classe con logica inline in execute()
     * passerebbe comunque), quindi la catena richiede di chiudersi esplicitamente con
     * {@link #withReceiver} oppure, se il ConcreteCommand è genuinamente privo di un
     * Receiver separato (self-contained command), con {@link #withoutReceiver}.
     */
    public CommandAssert withCommandInterface(Class<?> commandClass) {
        this.commandClass = commandClass;
        this.commandMetadata = ClassAnalyzer.analyze(commandClass);
        return this;
    }

    /**
     * Specifica il Receiver e scatta la verifica, inclusa la delega comportamentale.
     * Metodo terminale della catena.
     */
    public void withReceiver(Class<?> receiverClass) {
        requireCommandInterfaceDeclared();
        ClassMetadata receiverMetadata = ClassAnalyzer.analyze(receiverClass);
        List<String> violations = new CommandVerifier(concreteCommandMetadata, commandMetadata, receiverMetadata).verify();
        if (!violations.isEmpty()) {
            throw new AssertionError(formatViolations(receiverClass.getSimpleName(), violations));
        }
    }

    /**
     * Scatta la verifica senza controllare la delega a un Receiver — scelta esplicita per
     * un ConcreteCommand che non ha un Receiver separato (la logica dell'operazione è
     * contenuta nel comando stesso). A differenza di non chiamare affatto un metodo
     * terminale, questa è una dichiarazione intenzionale: chi legge il test vede che
     * l'assenza del Receiver è una scelta, non una dimenticanza. Metodo terminale della catena.
     */
    public void withoutReceiver() {
        requireCommandInterfaceDeclared();
        List<String> violations = new CommandVerifier(concreteCommandMetadata, commandMetadata).verify();
        if (!violations.isEmpty()) {
            throw new AssertionError(formatViolations(commandClass.getSimpleName(), violations));
        }
    }

    private void requireCommandInterfaceDeclared() {
        if (commandMetadata == null) {
            throw new IllegalStateException("Chiamare withCommandInterface() prima di withReceiver()/withoutReceiver()");
        }
    }

    private String formatViolations(String secondClassSimpleName, List<String> violations) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(concreteCommandClass.getSimpleName())
          .append(" / ").append(secondClassSimpleName)
          .append(": violazione pattern Command\n");
        violations.forEach(v -> sb.append("  - ").append(v).append("\n"));
        return sb.toString();
    }
}
