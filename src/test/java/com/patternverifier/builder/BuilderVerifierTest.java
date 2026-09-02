package com.patternverifier.builder;

import com.patternverifier.PatternAssertions;
import com.patternverifier.builder.correct.Person;
import com.patternverifier.builder.correct.PersonBuilder;
import com.patternverifier.builder.correct.Query;
import com.patternverifier.builder.correct.QueryBuilder;
import com.patternverifier.builder.wrong.AllViolationsBuilder;
import com.patternverifier.builder.wrong.CopyingBuilder;
import com.patternverifier.builder.wrong.NoBuildMethodBuilder;
import com.patternverifier.builder.wrong.NoFluentMethodBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BuilderVerifierTest {

    @Test
    void personBuilderShouldPass() {
        PatternAssertions.assertThat(PersonBuilder.class)
                .implementsBuilder()
                .forProduct(Person.class);
    }

    @Test
    void queryBuilderWithCreateShouldPass() {
        // Variante: naming create() invece di build(), metodi fluenti con nomi di dominio
        PatternAssertions.assertThat(QueryBuilder.class)
                .implementsBuilder()
                .forProduct(Query.class);
    }

    @Test
    void noFluentMethodBuilderShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(NoFluentMethodBuilder.class)
                        .implementsBuilder()
                        .forProduct(Person.class)
        );
        assertTrue(error.getMessage().contains("fluent") || error.getMessage().contains("catena"),
                "Il messaggio dovrebbe indicare la mancanza di metodi fluenti");
    }

    @Test
    void noBuildMethodBuilderShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(NoBuildMethodBuilder.class)
                        .implementsBuilder()
                        .forProduct(Person.class)
        );
        assertTrue(error.getMessage().contains("build") || error.getMessage().contains("terminale"),
                "Il messaggio dovrebbe indicare la mancanza del metodo build");
    }

    @Test
    void builderReturningNewInstanceShouldBeReported() {
        // I metodi dichiarano il tipo del Builder come tipo di ritorno — la catena compila — ma
        // restituiscono una nuova istanza invece di this. Fino al 2026-07-22 questo caso passava:
        // il verifier si fermava alla firma e non leggeva il corpo del metodo.
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(CopyingBuilder.class)
                        .implementsBuilder()
                        .forProduct(Person.class)
        );
        assertTrue(error.getMessage().contains("this"),
                "Il messaggio dovrebbe indicare che nessun metodo fluente restituisce this");
    }

    @Test
    void multipleViolationsShouldAllBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(AllViolationsBuilder.class)
                        .implementsBuilder()
                        .forProduct(Person.class)
        );
        String msg = error.getMessage();
        assertTrue(msg.contains("fluent") || msg.contains("catena"),
                "Dovrebbe riportare la mancanza di metodi fluenti");
        assertTrue(msg.contains("build") || msg.contains("terminale"),
                "Dovrebbe riportare la mancanza del metodo build");
    }
}
