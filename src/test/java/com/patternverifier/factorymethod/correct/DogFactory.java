package com.patternverifier.factorymethod.correct;

public class DogFactory extends AnimalFactory {
    @Override
    public Animal createAnimal() { return new Dog(); }
}
