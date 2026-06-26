package com.patternverifier.state.wrong;

import com.patternverifier.state.correct.LightState;

// Violazione: struttura corretta (campo + transizione) ma non invoca mai metodi su LightState.
public class NoStateInvocationContext {

    @SuppressWarnings("unused")
    private LightState currentState;

    public void setState(LightState state) {
        this.currentState = state;
    }

    public void change() {
        // non chiama mai currentState.handle() o altri metodi su LightState
    }
}
