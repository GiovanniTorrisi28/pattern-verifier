package com.patternverifier.bridge;

import com.patternverifier.PatternAssertions;
import com.patternverifier.bridge.correct.CircleShape;
import com.patternverifier.bridge.correct.Device;
import com.patternverifier.bridge.correct.DrawingAPI;
import com.patternverifier.bridge.correct.RemoteControl;
import com.patternverifier.bridge.wrong.ConcreteDevice;
import com.patternverifier.bridge.wrong.NoDelegatingAbstraction;
import com.patternverifier.bridge.wrong.NoImplementorFieldAbstraction;
import com.patternverifier.bridge.wrong.SameHierarchyAbstraction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BridgeVerifierTest {

    @Test
    void remoteControlWithDeviceInterfaceShouldPass() {
        PatternAssertions.assertThat(RemoteControl.class)
                .implementsBridge()
                .withImplementor(Device.class);
    }

    @Test
    void circleShapeWithAbstractDrawingAPIShouldPass() {
        // Variante: Implementor come classe astratta invece di interfaccia
        PatternAssertions.assertThat(CircleShape.class)
                .implementsBridge()
                .withImplementor(DrawingAPI.class);
    }

    @Test
    void concreteImplementorShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(RemoteControl.class)
                        .implementsBridge()
                        .withImplementor(ConcreteDevice.class)
        );
        assertTrue(error.getMessage().contains("interfaccia") || error.getMessage().contains("astratt"),
                "Il messaggio dovrebbe indicare che l'Implementor non è astratto");
    }

    @Test
    void noImplementorFieldShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(NoImplementorFieldAbstraction.class)
                        .implementsBridge()
                        .withImplementor(Device.class)
        );
        assertTrue(error.getMessage().contains("campo") || error.getMessage().contains("ponte"),
                "Il messaggio dovrebbe indicare la mancanza del campo Implementor");
    }

    @Test
    void sameHierarchyShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(SameHierarchyAbstraction.class)
                        .implementsBridge()
                        .withImplementor(Device.class)
        );
        assertTrue(error.getMessage().contains("indipendenti") || error.getMessage().contains("sovrappo"),
                "Il messaggio dovrebbe indicare che le due gerarchie non sono indipendenti");
    }

    @Test
    void multipleViolationsShouldAllBeReported() {
        // ConcreteDevice (Implementor non astratto → check 1) +
        // NoImplementorFieldAbstraction (nessun campo ConcreteDevice → check 2)
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(NoImplementorFieldAbstraction.class)
                        .implementsBridge()
                        .withImplementor(ConcreteDevice.class)
        );
        String msg = error.getMessage();
        assertTrue(msg.contains("interfaccia") || msg.contains("astratt"),
                "Dovrebbe riportare che l'Implementor non è astratto");
        assertTrue(msg.contains("campo") || msg.contains("ponte"),
                "Dovrebbe riportare la mancanza del campo Implementor");
        assertTrue(msg.contains("invoca"),
                "Dovrebbe riportare che l'Abstraction non invoca metodi sull'Implementor");
    }

    @Test
    void noDelegationAbstractionShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(NoDelegatingAbstraction.class)
                        .implementsBridge()
                        .withImplementor(Device.class)
        );
        assertTrue(error.getMessage().contains("invoca"),
                "Il messaggio dovrebbe indicare che l'Abstraction non invoca metodi sull'Implementor");
    }
}
