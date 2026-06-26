package com.patternverifier.strategy;

import com.patternverifier.PatternAssertions;
import com.patternverifier.strategy.correct.SortStrategy;
import com.patternverifier.strategy.correct.SorterWithConstructor;
import com.patternverifier.strategy.correct.SorterWithSetter;
import com.patternverifier.strategy.wrong.AllViolationsContext;
import com.patternverifier.strategy.wrong.ConcreteStrategy;
import com.patternverifier.strategy.wrong.EmptyStrategy;
import com.patternverifier.strategy.wrong.MissingFieldContext;
import com.patternverifier.strategy.wrong.MissingInjectionContext;
import com.patternverifier.strategy.wrong.NoStrategyInvocationContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StrategyVerifierTest {

    @Test
    void contextWithSetterShouldPass() {
        PatternAssertions.assertThat(SorterWithSetter.class)
                .implementsStrategy()
                .withStrategyInterface(SortStrategy.class);
    }

    @Test
    void contextWithConstructorInjectionShouldPass() {
        // Variante: Strategy iniettata via costruttore invece di setter
        PatternAssertions.assertThat(SorterWithConstructor.class)
                .implementsStrategy()
                .withStrategyInterface(SortStrategy.class);
    }

    @Test
    void concreteStrategyShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(SorterWithSetter.class)
                        .implementsStrategy()
                        .withStrategyInterface(ConcreteStrategy.class)
        );
        assertTrue(error.getMessage().contains("astratt") || error.getMessage().contains("interfaccia"),
                "Il messaggio dovrebbe indicare che la Strategy non è astratta");
    }

    @Test
    void emptyStrategyShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(SorterWithSetter.class)
                        .implementsStrategy()
                        .withStrategyInterface(EmptyStrategy.class)
        );
        assertTrue(error.getMessage().contains("metod"),
                "Il messaggio dovrebbe indicare che la Strategy non ha metodi");
    }

    @Test
    void missingFieldContextShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(MissingFieldContext.class)
                        .implementsStrategy()
                        .withStrategyInterface(SortStrategy.class)
        );
        assertTrue(error.getMessage().contains("campo"),
                "Il messaggio dovrebbe indicare il campo Strategy mancante");
    }

    @Test
    void missingInjectionContextShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(MissingInjectionContext.class)
                        .implementsStrategy()
                        .withStrategyInterface(SortStrategy.class)
        );
        assertTrue(error.getMessage().contains("setter") || error.getMessage().contains("costruttore"),
                "Il messaggio dovrebbe indicare che manca un punto di iniezione della Strategy");
    }

    @Test
    void multipleViolationsShouldAllBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(AllViolationsContext.class)
                        .implementsStrategy()
                        .withStrategyInterface(ConcreteStrategy.class)
        );
        String msg = error.getMessage();
        assertTrue(msg.contains("astratt") || msg.contains("interfaccia"),
                "Dovrebbe riportare che la Strategy non è astratta");
        assertTrue(msg.contains("campo"),
                "Dovrebbe riportare il campo Strategy mancante nel Context");
        assertTrue(msg.contains("setter") || msg.contains("costruttore"),
                "Dovrebbe riportare il punto di iniezione mancante nel Context");
        assertTrue(msg.contains("invoca"),
                "Dovrebbe riportare che il Context non invoca metodi su Strategy");
    }

    @Test
    void noStrategyInvocationShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(NoStrategyInvocationContext.class)
                        .implementsStrategy()
                        .withStrategyInterface(SortStrategy.class)
        );
        assertTrue(error.getMessage().contains("invoca"),
                "Il messaggio dovrebbe indicare che il Context non invoca metodi su Strategy");
    }
}
