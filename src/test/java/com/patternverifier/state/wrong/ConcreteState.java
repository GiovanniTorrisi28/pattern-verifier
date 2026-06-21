package com.patternverifier.state.wrong;

// VIOLAZIONE 1: State concreto — non è né interfaccia né classe astratta
public class ConcreteState {
    public void handle() {
        System.out.println("stato concreto");
    }
}
