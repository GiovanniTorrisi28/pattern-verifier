package com.patternverifier.chainofresponsibility.wrong;

// VIOLAZIONE 2: Handler senza self-reference — manca il campo del proprio tipo
// che rappresenta il successore nella catena
public abstract class NoNextFieldHandler {

    @SuppressWarnings("unused")
    private String name;

    public abstract void handleRequest(String request);
}
