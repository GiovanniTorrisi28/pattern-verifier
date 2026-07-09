package com.patternverifier.verifiers;

import com.patternverifier.core.ClassMetadata;
import com.patternverifier.core.MethodInvocationAnalyzer;
import com.patternverifier.core.TypeHierarchy;

import java.util.ArrayList;
import java.util.List;

public class CommandVerifier {

    private final ClassMetadata concreteCommand;
    private final ClassMetadata command;
    private final ClassMetadata receiver;

    public CommandVerifier(ClassMetadata concreteCommand, ClassMetadata command) {
        this(concreteCommand, command, null);
    }

    public CommandVerifier(ClassMetadata concreteCommand, ClassMetadata command, ClassMetadata receiver) {
        this.concreteCommand = concreteCommand;
        this.command = command;
        this.receiver = receiver;
    }

    public List<String> verify() {
        List<String> violations = new ArrayList<>();
        checkCommandIsAbstract(violations);
        checkCommandHasExecuteMethod(violations);
        checkConcreteCommandImplementsCommand(violations);
        checkConcreteCommandOverridesExecute(violations);
        if (receiver != null) {
            checkConcreteCommandDelegatesToReceiver(violations);
        }
        return violations;
    }

    private void checkCommandIsAbstract(List<String> violations) {
        if (!command.isInterface() && !command.isAbstract()) {
            violations.add(command.getSimpleName()
                    + " non è né un'interfaccia né una classe astratta"
                    + " — il Command deve definire un contratto astratto per le richieste");
        }
    }

    // Naming convention GoF per il metodo di esecuzione: execute*, run*, perform*, invoke*
    private void checkCommandHasExecuteMethod(List<String> violations) {
        boolean found = command.getMethods().stream()
                .filter(m -> !m.isConstructor())
                .anyMatch(m -> m.getName().startsWith("execute")
                        || m.getName().startsWith("run")
                        || m.getName().startsWith("perform")
                        || m.getName().startsWith("invoke"));
        if (!found) {
            violations.add(command.getSimpleName()
                    + " non ha un metodo di esecuzione con naming convention Command"
                    + " (execute*, run*, perform*, invoke*)");
        }
    }

    private void checkConcreteCommandImplementsCommand(List<String> violations) {
        if (!TypeHierarchy.isAssignable(concreteCommand.getClassName(), command.getClassName())) {
            violations.add(concreteCommand.getSimpleName()
                    + " non implementa né estende "
                    + command.getSimpleName()
                    + " — il ConcreteCommand deve implementare l'interfaccia Command");
        }
    }

    private void checkConcreteCommandOverridesExecute(List<String> violations) {
        boolean found = command.getMethods().stream()
                .filter(m -> !m.isConstructor())
                .filter(m -> m.getName().startsWith("execute")
                        || m.getName().startsWith("run")
                        || m.getName().startsWith("perform")
                        || m.getName().startsWith("invoke"))
                .anyMatch(executeMethod ->
                        concreteCommand.getMethods().stream()
                                .anyMatch(m -> m.getName().equals(executeMethod.getName())
                                        && !m.isAbstract()));
        if (!found) {
            violations.add(concreteCommand.getSimpleName()
                    + " non ha un'implementazione concreta del metodo di esecuzione"
                    + " — il ConcreteCommand deve fornire il comportamento specifico");
        }
    }

    // La proprietà centrale del Command GoF: ConcreteCommand non contiene logica inline
    // ma delega l'operazione reale al Receiver. Questa separazione permette all'Invoker
    // di eseguire comandi senza conoscere chi esegue l'operazione reale.
    private void checkConcreteCommandDelegatesToReceiver(List<String> violations) {
        if (!MethodInvocationAnalyzer.invokesMethodsOn(concreteCommand.getClassName(), receiver.getClassName())) {
            violations.add(concreteCommand.getSimpleName()
                    + " non invoca mai metodi sul Receiver "
                    + receiver.getSimpleName()
                    + " — il ConcreteCommand deve delegare l'esecuzione al Receiver,"
                    + " non contenere la logica inline");
        }
    }
}
