package com.patternverifier.composite.wrong;

// VIOLAZIONE 1: non implementa FileSystemItem (Component)
// VIOLAZIONE 2: nessun campo di tipo Collection
// VIOLAZIONE 3: nessun metodo add*(FileSystemItem)
public class AllViolationsComposite {

    @SuppressWarnings("unused")
    private final String name;

    public AllViolationsComposite(String name) {
        this.name = name;
    }
}
