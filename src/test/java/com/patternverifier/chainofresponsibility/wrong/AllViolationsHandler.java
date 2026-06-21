package com.patternverifier.chainofresponsibility.wrong;

// VIOLAZIONI 1+2+3: concreto, senza self-reference, senza handle method
public class AllViolationsHandler {

    @SuppressWarnings("unused")
    private String name;

    public void doWork(String input) {
        System.out.println("nessuna catena: " + input);
    }
}
