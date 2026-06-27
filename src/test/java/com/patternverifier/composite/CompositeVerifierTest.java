package com.patternverifier.composite;

import com.patternverifier.PatternAssertions;
import com.patternverifier.composite.correct.FileSystemDirectory;
import com.patternverifier.composite.correct.FileSystemItem;
import com.patternverifier.composite.correct.MenuGroup;
import com.patternverifier.composite.wrong.AllViolationsComposite;
import com.patternverifier.composite.wrong.MissingAddMethodComposite;
import com.patternverifier.composite.wrong.MissingCollectionComposite;
import com.patternverifier.composite.wrong.MissingInterfaceComposite;
import com.patternverifier.composite.wrong.NoDelegatingComposite;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompositeVerifierTest {

    @Test
    void correctCompositeShouldPass() {
        PatternAssertions.assertThat(FileSystemDirectory.class)
                .implementsComposite()
                .forComponent(FileSystemItem.class);
    }

    @Test
    void compositeWithSetAndDifferentAddNameShouldPass() {
        // Variante: Set invece di List, metodo addItem invece di addChild
        PatternAssertions.assertThat(MenuGroup.class)
                .implementsComposite()
                .forComponent(FileSystemItem.class);
    }

    @Test
    void missingInterfaceShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(MissingInterfaceComposite.class)
                        .implementsComposite()
                        .forComponent(FileSystemItem.class)
        );
        assertTrue(error.getMessage().contains("non implementa"),
                "Il messaggio dovrebbe indicare che il Composite non implementa il Component");
    }

    @Test
    void missingCollectionFieldShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(MissingCollectionComposite.class)
                        .implementsComposite()
                        .forComponent(FileSystemItem.class)
        );
        assertTrue(error.getMessage().contains("Collection"),
                "Il messaggio dovrebbe indicare il campo Collection mancante");
    }

    @Test
    void missingAddMethodShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(MissingAddMethodComposite.class)
                        .implementsComposite()
                        .forComponent(FileSystemItem.class)
        );
        assertTrue(error.getMessage().contains("add"),
                "Il messaggio dovrebbe indicare il metodo add* mancante");
    }

    @Test
    void multipleViolationsShouldAllBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(AllViolationsComposite.class)
                        .implementsComposite()
                        .forComponent(FileSystemItem.class)
        );
        String msg = error.getMessage();
        assertTrue(msg.contains("non implementa"), "Dovrebbe riportare la mancanza dell'interfaccia Component");
        assertTrue(msg.contains("Collection"),     "Dovrebbe riportare il campo Collection mancante");
        assertTrue(msg.contains("add"),            "Dovrebbe riportare il metodo add* mancante");
        assertTrue(msg.contains("invoca"),
                "Dovrebbe riportare che il Composite non delega ai figli");
    }

    @Test
    void noDelegationCompositeShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(NoDelegatingComposite.class)
                        .implementsComposite()
                        .forComponent(FileSystemItem.class)
        );
        assertTrue(error.getMessage().contains("invoca"),
                "Il messaggio dovrebbe indicare che il Composite non invoca metodi sui figli");
    }
}
