package com.patternverifier.factorymethod.wrong;

import com.patternverifier.factorymethod.correct.Animal;
import com.patternverifier.factorymethod.correct.Dog;

// VIOLAZIONE: non estende AnimalFactory — non ha il Creator corretto come superclasse
public class WrongParentFactory {
    public Animal createAnimal() { return new Dog(); }
}
