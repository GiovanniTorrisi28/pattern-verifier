package com.patternverifier.state.correct;

public class TrafficLight {

    private LightState currentState;

    public TrafficLight(LightState initialState) {
        this.currentState = initialState;
    }

    public void setState(LightState state) {
        this.currentState = state;
    }

    public void change() {
        currentState.handle(this);
    }
}
