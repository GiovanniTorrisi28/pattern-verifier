package com.patternverifier.factorymethod.wrong;

// Usato come ConcreteCreator nel test delle violazioni multiple.
// VIOLAZIONE 3 (ConcreteCreator): non estende AnimalFactory
// VIOLAZIONE 4 (ConcreteCreator): non fa override di createAnimal
public class AllViolationsFactory {
    public void doSomethingElse() {}
}
