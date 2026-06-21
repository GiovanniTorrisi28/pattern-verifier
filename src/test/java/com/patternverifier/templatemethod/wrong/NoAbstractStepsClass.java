package com.patternverifier.templatemethod.wrong;

// VIOLAZIONE 3: classe astratta senza metodi astratti — nessun passo da implementare nelle sottoclassi
public abstract class NoAbstractStepsClass {

    public void process() {
        step1();
        step2();
    }

    private void step1() {
        System.out.println("passo 1 concreto");
    }

    private void step2() {
        System.out.println("passo 2 concreto");
    }
}
