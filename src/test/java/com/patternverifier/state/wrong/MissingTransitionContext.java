package com.patternverifier.state.wrong;

import com.patternverifier.state.correct.LightState;

// VIOLAZIONE 4: Context con campo State ma senza metodo di transizione
// Non permette di cambiare lo stato corrente dall'esterno
public class MissingTransitionContext {

    @SuppressWarnings("unused")
    private LightState currentState;

    public void doAction() {
        System.out.println("azione fissa senza transizione di stato");
    }
}
