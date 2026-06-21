package com.patternverifier.factorymethod.wrong;

import com.patternverifier.factorymethod.correct.Animal;

// VIOLAZIONE: Creator non è astratto — non può delegare la creazione alle sottoclassi
public class NotAbstractCreator {
    public Animal createAnimal() { return null; }
}
