package com.patternverifier.factorymethod.batch;

import com.patternverifier.factorymethod.correct.Animal;

import java.util.List;

/**
 * Variante "a lotti" del Factory Method: il metodo astratto restituisce una collezione di
 * Product anziché un singolo Product (es. {@code Vector<Handle> handles()} in JHotDraw).
 */
public abstract class AnimalKennel {
    public abstract List<Animal> createAnimals();
}
