package com.patternverifier.templatemethod;

import com.patternverifier.PatternAssertions;
import com.patternverifier.templatemethod.correct.BeverageMaker;
import com.patternverifier.templatemethod.correct.DataProcessor;
import com.patternverifier.templatemethod.wrong.AllAbstractClass;
import com.patternverifier.templatemethod.wrong.ConcreteClass;
import com.patternverifier.templatemethod.wrong.NoAbstractStepsClass;
import com.patternverifier.templatemethod.wrong.NonCallingTemplateMethod;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TemplateMethodVerifierTest {

    @Test
    void dataProcessorShouldPass() {
        PatternAssertions.assertThat(DataProcessor.class)
                .implementsTemplateMethod()
                .withTemplateMethod("process");
    }

    @Test
    void beverageMakerShouldPass() {
        // Variante: template method con passi privati intermedi (boilWater, pourInCup)
        // e passi astratti (brew, addCondiments) — il body analyzer rileva correttamente
        // solo le INVOKEVIRTUAL verso la stessa classe, ignorando le chiamate private
        PatternAssertions.assertThat(BeverageMaker.class)
                .implementsTemplateMethod()
                .withTemplateMethod("prepare");
    }

    @Test
    void concreteClassShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(ConcreteClass.class)
                        .implementsTemplateMethod()
                        .withTemplateMethod("process")
        );
        assertTrue(error.getMessage().contains("astratta"),
                "Il messaggio dovrebbe indicare che la classe non è astratta");
    }

    @Test
    void allAbstractClassShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(AllAbstractClass.class)
                        .implementsTemplateMethod()
                        .withTemplateMethod("step1")
        );
        assertTrue(error.getMessage().contains("concreti") || error.getMessage().contains("scheletro"),
                "Il messaggio dovrebbe indicare la mancanza di un metodo concreto (template method)");
    }

    @Test
    void noAbstractStepsShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(NoAbstractStepsClass.class)
                        .implementsTemplateMethod()
                        .withTemplateMethod("process")
        );
        assertTrue(error.getMessage().contains("astratti") || error.getMessage().contains("delega"),
                "Il messaggio dovrebbe indicare la mancanza di passi astratti");
    }

    @Test
    void nonCallingTemplateMethodShouldBeReported() {
        // Check 4 — l'unico che richiede analisi del corpo del metodo con MethodVisitor
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(NonCallingTemplateMethod.class)
                        .implementsTemplateMethod()
                        .withTemplateMethod("process")
        );
        assertTrue(error.getMessage().contains("invoca") || error.getMessage().contains("richiama"),
                "Il messaggio dovrebbe indicare che il template method non chiama i passi astratti");
    }

    @Test
    void multipleViolationsShouldAllBeReported() {
        // ConcreteClass fallisce: check 1 (non astratta), check 3 (nessun passo astratto),
        // check 4 (il template method non chiama passi astratti inesistenti)
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(ConcreteClass.class)
                        .implementsTemplateMethod()
                        .withTemplateMethod("process")
        );
        String msg = error.getMessage();
        assertTrue(msg.contains("astratta"),
                "Dovrebbe riportare la mancanza di classe astratta");
        assertTrue(msg.contains("astratti") || msg.contains("invoca"),
                "Dovrebbe riportare violazioni aggiuntive (passi astratti o chiamate)");
    }
}
