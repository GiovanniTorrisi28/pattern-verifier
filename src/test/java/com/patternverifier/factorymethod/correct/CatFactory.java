package com.patternverifier.factorymethod.correct;

// Variante: secondo ConcreteCreator — verifica che il tool accetti più implementazioni dello stesso Creator.
public class CatFactory extends AnimalFactory {
    @Override
    public Animal createAnimal() { return new Cat(); }
}
