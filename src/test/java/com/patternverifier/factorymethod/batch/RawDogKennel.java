package com.patternverifier.factorymethod.batch;

import com.patternverifier.factorymethod.correct.Dog;

import java.util.Vector;

public class RawDogKennel extends RawAnimalKennel {
    @Override
    @SuppressWarnings("rawtypes")
    public Vector createAnimals() {
        Vector v = new Vector();
        v.add(new Dog());
        return v;
    }
}
