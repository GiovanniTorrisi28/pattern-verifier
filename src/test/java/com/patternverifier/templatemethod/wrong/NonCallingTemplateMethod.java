package com.patternverifier.templatemethod.wrong;

// VIOLAZIONE 4: ha la struttura corretta (classe astratta con metodi astratti
// e un metodo concreto) ma il "template method" non richiama i passi astratti
// nel suo corpo — non rispetta lo scheletro algoritmico del pattern.
public abstract class NonCallingTemplateMethod {

    protected abstract void step1();
    protected abstract void step2();

    public void process() {
        // Non chiama step1() né step2() — manca la delega ai passi astratti
        System.out.println("algoritmo hardcoded senza delegare ai passi");
    }
}
