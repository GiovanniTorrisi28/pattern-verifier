package com.patternverifier.factorymethod.correct;

// Variante: ConcreteCreator che implementa un'interfaccia Creator (non una classe astratta).
public class BirdFactory implements AnimalCreator {
    @Override
    public Animal createAnimal() { return () -> "tweet"; }
}
