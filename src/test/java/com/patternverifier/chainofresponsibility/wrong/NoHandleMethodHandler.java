package com.patternverifier.chainofresponsibility.wrong;

// VIOLAZIONE 3: Handler astratto con self-reference ma senza metodo di gestione
// Non rispetta la naming convention del pattern
public abstract class NoHandleMethodHandler {

    protected NoHandleMethodHandler next;

    public void setSuccessor(NoHandleMethodHandler successor) {
        this.next = successor;
    }

    public abstract void doWork(String input);
}
