package com.patternverifier.factorymethod.correct;

import com.patternverifier.annotations.GoFFactoryMethod;

@GoFFactoryMethod(creator = AnimalFactory.class, product = Animal.class, factoryMethod = "createAnimal")
public class DogFactory extends AnimalFactory {
    @Override
    public Animal createAnimal() { return new Dog(); }
}
