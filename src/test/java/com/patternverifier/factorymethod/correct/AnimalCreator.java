package com.patternverifier.factorymethod.correct;

// Variante: Creator come interfaccia invece di classe astratta.
// Verifica che checkConcreteCreatorExtendsCreator gestisca correttamente
// la relazione implements (non solo extends).
public interface AnimalCreator {
    Animal createAnimal();
}
