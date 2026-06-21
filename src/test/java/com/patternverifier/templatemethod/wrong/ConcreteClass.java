package com.patternverifier.templatemethod.wrong;

// VIOLAZIONE 1: non è una classe astratta
// Conseguenza: non può avere metodi astratti (→ violazione 3)
// Conseguenza: il template method non chiama passi astratti (→ violazione 4)
public class ConcreteClass {

    public void process() {
        System.out.println("implementazione diretta senza passi astratti");
    }
}
