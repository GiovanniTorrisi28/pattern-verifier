package com.patternverifier.chainofresponsibility.wrong;

// VIOLAZIONE 1: Handler concreto — non è né interfaccia né classe astratta
public class ConcreteHandlerOnly {

    protected ConcreteHandlerOnly next;

    public void handleRequest(String request) {
        if (next != null) next.handleRequest(request);
    }
}
