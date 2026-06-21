package com.patternverifier.abstractfactory.wrong;

// Usato come ConcreteFactory nel test delle violazioni multiple.
// VIOLAZIONE 3: non implementa nessuna AbstractFactory
// VIOLAZIONE 4: non ha factory method per nessun prodotto
public class AllViolationsConcreteFactory {
    public void doSomethingElse() {}
}
