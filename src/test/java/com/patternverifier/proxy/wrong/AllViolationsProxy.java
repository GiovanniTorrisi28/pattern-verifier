package com.patternverifier.proxy.wrong;

// VIOLAZIONE 1: non implementa Image (Subject)
// VIOLAZIONE 2: nessun campo di tipo Image o RealImage
public class AllViolationsProxy {

    @SuppressWarnings("unused")
    private String description;

    public AllViolationsProxy(String description) {
        this.description = description;
    }
}
