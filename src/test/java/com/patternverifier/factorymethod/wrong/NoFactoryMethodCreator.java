package com.patternverifier.factorymethod.wrong;

// VIOLAZIONE: Creator è astratto ma non ha il factory method dichiarato
public abstract class NoFactoryMethodCreator {
    public void doSomething() {}
}
