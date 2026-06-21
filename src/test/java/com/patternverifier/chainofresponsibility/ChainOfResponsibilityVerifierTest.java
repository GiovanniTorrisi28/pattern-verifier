package com.patternverifier.chainofresponsibility;

import com.patternverifier.PatternAssertions;
import com.patternverifier.chainofresponsibility.correct.LogHandler;
import com.patternverifier.chainofresponsibility.correct.RequestHandler;
import com.patternverifier.chainofresponsibility.wrong.AllViolationsHandler;
import com.patternverifier.chainofresponsibility.wrong.ConcreteHandlerOnly;
import com.patternverifier.chainofresponsibility.wrong.NoHandleMethodHandler;
import com.patternverifier.chainofresponsibility.wrong.NoNextFieldHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChainOfResponsibilityVerifierTest {

    @Test
    void requestHandlerShouldPass() {
        PatternAssertions.assertThat(RequestHandler.class)
                .implementsChainOfResponsibility();
    }

    @Test
    void logHandlerInterfaceShouldPass() {
        // Variante: interfaccia con campo LogHandler come costante di interfaccia
        PatternAssertions.assertThat(LogHandler.class)
                .implementsChainOfResponsibility();
    }

    @Test
    void concreteHandlerShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(ConcreteHandlerOnly.class)
                        .implementsChainOfResponsibility()
        );
        assertTrue(error.getMessage().contains("interfaccia") || error.getMessage().contains("astratt"),
                "Il messaggio dovrebbe indicare che il Handler non è astratto");
    }

    @Test
    void noNextFieldShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(NoNextFieldHandler.class)
                        .implementsChainOfResponsibility()
        );
        assertTrue(error.getMessage().contains("self") || error.getMessage().contains("successore"),
                "Il messaggio dovrebbe indicare la mancanza del campo self-reference");
    }

    @Test
    void noHandleMethodShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(NoHandleMethodHandler.class)
                        .implementsChainOfResponsibility()
        );
        assertTrue(error.getMessage().contains("handle") || error.getMessage().contains("gestione"),
                "Il messaggio dovrebbe indicare la mancanza del metodo di gestione");
    }

    @Test
    void multipleViolationsShouldAllBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(AllViolationsHandler.class)
                        .implementsChainOfResponsibility()
        );
        String msg = error.getMessage();
        assertTrue(msg.contains("interfaccia") || msg.contains("astratt"),
                "Dovrebbe riportare che il Handler non è astratto");
        assertTrue(msg.contains("self") || msg.contains("successore"),
                "Dovrebbe riportare la mancanza della self-reference");
        assertTrue(msg.contains("handle") || msg.contains("gestione"),
                "Dovrebbe riportare la mancanza del metodo di gestione");
    }
}
