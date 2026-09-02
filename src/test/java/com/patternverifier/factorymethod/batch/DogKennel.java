package com.patternverifier.factorymethod.batch;

import com.patternverifier.factorymethod.correct.Animal;
import com.patternverifier.factorymethod.correct.Dog;

import java.util.List;

public class DogKennel extends AnimalKennel {
    @Override
    public List<Animal> createAnimals() {
        return List.of(new Dog(), new Dog());
    }
}
