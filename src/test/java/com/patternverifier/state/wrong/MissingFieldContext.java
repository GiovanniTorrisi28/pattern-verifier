package com.patternverifier.state.wrong;

import com.patternverifier.state.correct.LightState;

// VIOLAZIONE 3: Context senza campo di tipo State
// Non mantiene un riferimento allo stato corrente
public class MissingFieldContext {

    public void setState(LightState state) {
        // riceve lo stato ma non lo memorizza
    }
}
