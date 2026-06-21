package com.patternverifier.bridge.wrong;

// VIOLAZIONE 1: Implementor concreto — non è né un'interfaccia né una classe astratta
public class ConcreteDevice {
    public void turnOn() { System.out.println("on"); }
    public void turnOff() { System.out.println("off"); }
    public void setVolume(int v) { System.out.println("vol: " + v); }
}
