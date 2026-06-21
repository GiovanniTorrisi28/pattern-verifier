package com.patternverifier.singleton;

import com.patternverifier.PatternAssertions;
import com.patternverifier.singleton.correct.DatabaseConnection;
import com.patternverifier.singleton.correct.LazySingleton;
import com.patternverifier.singleton.wrong.MissingGetterSingleton;
import com.patternverifier.singleton.wrong.MissingInstanceFieldSingleton;
import com.patternverifier.singleton.wrong.MultipleViolationsSingleton;
import com.patternverifier.singleton.wrong.PublicConstructorSingleton;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SingletonVerifierTest {

    @Test
    void correctSingletonShouldPass() {
        PatternAssertions.assertThat(DatabaseConnection.class)
                .implementsSingleton();
    }

    @Test
    void publicConstructorShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(PublicConstructorSingleton.class)
                        .implementsSingleton()
        );
        assertTrue(error.getMessage().contains("costruttore"),
                "Il messaggio dovrebbe indicare il problema con il costruttore");
    }

    @Test
    void missingInstanceFieldShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(MissingInstanceFieldSingleton.class)
                        .implementsSingleton()
        );
        assertTrue(error.getMessage().contains("campo static"),
                "Il messaggio dovrebbe indicare il campo static mancante");
    }

    @Test
    void missingGetterMethodShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(MissingGetterSingleton.class)
                        .implementsSingleton()
        );
        assertTrue(error.getMessage().contains("metodo static"),
                "Il messaggio dovrebbe indicare il metodo static getter mancante");
    }

    @Test
    void lazySingletonWithDifferentGetterNameShouldPass() {
        PatternAssertions.assertThat(LazySingleton.class)
                .implementsSingleton();
    }

    @Test
    void multipleViolationsShouldAllBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(MultipleViolationsSingleton.class)
                        .implementsSingleton()
        );
        String msg = error.getMessage();
        assertTrue(msg.contains("costruttore"), "Dovrebbe riportare la violazione del costruttore");
        assertTrue(msg.contains("campo static"),  "Dovrebbe riportare la violazione del campo static");
        assertTrue(msg.contains("metodo static"), "Dovrebbe riportare la violazione del metodo static");
    }
}
