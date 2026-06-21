package com.patternverifier.command.wrong;

import com.patternverifier.command.correct.Command;

// VIOLAZIONE 4: implementa Command ma non fornisce implementazione concreta di execute().
// Deve essere abstract: Java non consente classi concrete che implementano
// un'interfaccia senza implementare tutti i metodi astratti.
public abstract class AbstractConcreteCommand implements Command {
    public void doSomethingElse() {}
    // execute() rimane astratto
}
