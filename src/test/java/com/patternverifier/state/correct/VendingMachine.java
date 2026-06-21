package com.patternverifier.state.correct;

public class VendingMachine {

    private VendingState state;

    public VendingMachine(VendingState initialState) {
        this.state = initialState;
    }

    public void setState(VendingState state) {
        this.state = state;
    }

    public void insertCoin() {
        state.insertCoin();
    }
}
