package com.patternverifier.observer.wrong;

// VIOLAZIONE 1: non è né un'interfaccia né una classe astratta
public class ConcreteObserver {
    public void onEvent(String event) {}
}
