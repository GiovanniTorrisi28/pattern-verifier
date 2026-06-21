package com.patternverifier.bridge.wrong;

// VIOLAZIONE 2: Abstraction senza campo di tipo Implementor
// Non contiene un riferimento a Device — manca il "ponte" tra le gerarchie
public class NoImplementorFieldAbstraction {

    public void doSomething() {
        System.out.println("azione diretta senza delegare all'Implementor");
    }
}
