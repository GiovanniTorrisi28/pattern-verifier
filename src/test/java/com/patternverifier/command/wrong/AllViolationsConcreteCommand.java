package com.patternverifier.command.wrong;

// Usato come ConcreteCommand nel test delle violazioni multiple.
// VIOLAZIONE 3: non implementa nessuna interfaccia Command
// VIOLAZIONE 4: non ha implementazione del metodo di esecuzione
public class AllViolationsConcreteCommand {
    public void doSomethingElse() {}
}
