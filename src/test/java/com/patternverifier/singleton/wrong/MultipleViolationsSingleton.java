package com.patternverifier.singleton.wrong;

public class MultipleViolationsSingleton {

    // VIOLAZIONE 1: costruttore public
    // VIOLAZIONE 2: nessun campo static del proprio tipo
    // VIOLAZIONE 3: nessun metodo static getter

    public MultipleViolationsSingleton() {}
}
