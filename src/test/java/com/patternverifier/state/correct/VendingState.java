package com.patternverifier.state.correct;

// Variante: State come classe astratta invece di interfaccia
public abstract class VendingState {
    public abstract void insertCoin();
    public abstract void pressButton();
}
