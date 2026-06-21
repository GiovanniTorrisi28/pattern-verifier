package com.patternverifier.strategy.wrong;

// Usato come Context nel test delle violazioni multiple.
// VIOLAZIONE 3: non ha campo di tipo Strategy
// VIOLAZIONE 4: non ha setter né costruttore che accetti Strategy
public class AllViolationsContext {
    public void doSomething() {}
}
