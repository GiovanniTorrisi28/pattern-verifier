package com.patternverifier.state;

import com.patternverifier.PatternAssertions;
import com.patternverifier.state.correct.LightState;
import com.patternverifier.state.correct.TrafficLight;
import com.patternverifier.state.correct.VendingMachine;
import com.patternverifier.state.correct.VendingState;
import com.patternverifier.state.wrong.AllViolationsContext;
import com.patternverifier.state.wrong.ConcreteState;
import com.patternverifier.state.wrong.EmptyState;
import com.patternverifier.state.wrong.MissingFieldContext;
import com.patternverifier.state.wrong.MissingTransitionContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StateVerifierTest {

    @Test
    void trafficLightShouldPass() {
        PatternAssertions.assertThat(TrafficLight.class)
                .implementsState()
                .withStateInterface(LightState.class);
    }

    @Test
    void vendingMachineWithAbstractStateShouldPass() {
        // Variante: State come classe astratta invece di interfaccia
        PatternAssertions.assertThat(VendingMachine.class)
                .implementsState()
                .withStateInterface(VendingState.class);
    }

    @Test
    void concreteStateShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(TrafficLight.class)
                        .implementsState()
                        .withStateInterface(ConcreteState.class)
        );
        assertTrue(error.getMessage().contains("interfaccia") || error.getMessage().contains("astratt"),
                "Il messaggio dovrebbe indicare che lo State non è astratto");
    }

    @Test
    void emptyStateShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(TrafficLight.class)
                        .implementsState()
                        .withStateInterface(EmptyState.class)
        );
        assertTrue(error.getMessage().contains("metodi") || error.getMessage().contains("comportamento"),
                "Il messaggio dovrebbe indicare la mancanza di metodi nello State");
    }

    @Test
    void missingFieldContextShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(MissingFieldContext.class)
                        .implementsState()
                        .withStateInterface(LightState.class)
        );
        assertTrue(error.getMessage().contains("campo") || error.getMessage().contains("riferimento"),
                "Il messaggio dovrebbe indicare la mancanza del campo State nel Context");
    }

    @Test
    void missingTransitionContextShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(MissingTransitionContext.class)
                        .implementsState()
                        .withStateInterface(LightState.class)
        );
        assertTrue(error.getMessage().contains("transizione") || error.getMessage().contains("parametro"),
                "Il messaggio dovrebbe indicare la mancanza del metodo di transizione");
    }

    @Test
    void multipleViolationsShouldAllBeReported() {
        // ConcreteState (violazione 1) + AllViolationsContext (violazioni 3+4)
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(AllViolationsContext.class)
                        .implementsState()
                        .withStateInterface(ConcreteState.class)
        );
        String msg = error.getMessage();
        assertTrue(msg.contains("interfaccia") || msg.contains("astratt"),
                "Dovrebbe riportare che lo State non è astratto");
        assertTrue(msg.contains("campo") || msg.contains("transizione"),
                "Dovrebbe riportare violazioni del Context");
    }
}
