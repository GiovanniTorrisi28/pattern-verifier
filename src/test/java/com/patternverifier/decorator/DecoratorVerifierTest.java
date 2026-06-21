package com.patternverifier.decorator;

import com.patternverifier.PatternAssertions;
import com.patternverifier.decorator.correct.BoldDecorator;
import com.patternverifier.decorator.correct.ItalicDecorator;
import com.patternverifier.decorator.correct.TextComponent;
import com.patternverifier.decorator.wrong.AllViolationsDecorator;
import com.patternverifier.decorator.wrong.MissingConstructorDecorator;
import com.patternverifier.decorator.wrong.MissingFieldDecorator;
import com.patternverifier.decorator.wrong.MissingInterfaceDecorator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DecoratorVerifierTest {

    @Test
    void correctDecoratorShouldPass() {
        PatternAssertions.assertThat(BoldDecorator.class)
                .implementsDecorator()
                .forComponent(TextComponent.class);
    }

    @Test
    void missingInterfaceShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(MissingInterfaceDecorator.class)
                        .implementsDecorator()
                        .forComponent(TextComponent.class)
        );
        assertTrue(error.getMessage().contains("non implementa"),
                "Il messaggio dovrebbe indicare che il Decorator non implementa il Component");
    }

    @Test
    void missingComponentFieldShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(MissingFieldDecorator.class)
                        .implementsDecorator()
                        .forComponent(TextComponent.class)
        );
        assertTrue(error.getMessage().contains("campo"),
                "Il messaggio dovrebbe indicare il campo Component mancante");
    }

    @Test
    void missingConstructorShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(MissingConstructorDecorator.class)
                        .implementsDecorator()
                        .forComponent(TextComponent.class)
        );
        assertTrue(error.getMessage().contains("costruttore"),
                "Il messaggio dovrebbe indicare il costruttore mancante");
    }

    @Test
    void decoratorWithExtraFieldsShouldPass() {
        // Variante: Decorator con campi aggiuntivi oltre al Component
        PatternAssertions.assertThat(ItalicDecorator.class)
                .implementsDecorator()
                .forComponent(TextComponent.class);
    }

    @Test
    void multipleViolationsShouldAllBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(AllViolationsDecorator.class)
                        .implementsDecorator()
                        .forComponent(TextComponent.class)
        );
        String msg = error.getMessage();
        assertTrue(msg.contains("non implementa"), "Dovrebbe riportare la mancanza dell'interfaccia Component");
        assertTrue(msg.contains("campo"),           "Dovrebbe riportare il campo Component mancante");
        assertTrue(msg.contains("costruttore"),     "Dovrebbe riportare il costruttore mancante");
    }
}
