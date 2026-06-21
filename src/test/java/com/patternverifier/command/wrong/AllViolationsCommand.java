package com.patternverifier.command.wrong;

// Usato come Command interface nel test delle violazioni multiple.
// VIOLAZIONE 1: non è né un'interfaccia né una classe astratta
// VIOLAZIONE 2: non ha metodi con naming convention execute*/run*/perform*/invoke*
public class AllViolationsCommand {
    public void doSomethingElse() {}
}
