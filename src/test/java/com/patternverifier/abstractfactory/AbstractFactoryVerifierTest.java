package com.patternverifier.abstractfactory;

import com.patternverifier.PatternAssertions;
import com.patternverifier.abstractfactory.correct.AbstractUIFactory;
import com.patternverifier.abstractfactory.correct.Button;
import com.patternverifier.abstractfactory.correct.DarkThemeFactory;
import com.patternverifier.abstractfactory.correct.LightThemeFactory;
import com.patternverifier.abstractfactory.correct.TextField;
import com.patternverifier.abstractfactory.correct.UIFactory;
import com.patternverifier.abstractfactory.wrong.AllViolationsAbstractFactory;
import com.patternverifier.abstractfactory.wrong.AllViolationsConcreteFactory;
import com.patternverifier.abstractfactory.wrong.MissingMethodConcreteFactory;
import com.patternverifier.abstractfactory.wrong.NotAbstractFactory;
import com.patternverifier.abstractfactory.wrong.SingleMethodFactory;
import com.patternverifier.abstractfactory.wrong.WrongParentConcreteFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbstractFactoryVerifierTest {

    @Test
    void abstractFactoryAsInterfaceShouldPass() {
        PatternAssertions.assertThat(UIFactory.class)
                .implementsAbstractFactory()
                .producing(Button.class, TextField.class)
                .withConcreteFactory(LightThemeFactory.class);
    }

    @Test
    void abstractFactoryAsAbstractClassShouldPass() {
        // Variante: AbstractFactory come classe astratta invece di interfaccia
        PatternAssertions.assertThat(AbstractUIFactory.class)
                .implementsAbstractFactory()
                .producing(Button.class, TextField.class)
                .withConcreteFactory(DarkThemeFactory.class);
    }

    @Test
    void notAbstractFactoryShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(NotAbstractFactory.class)
                        .implementsAbstractFactory()
                        .producing(Button.class, TextField.class)
                        .withConcreteFactory(LightThemeFactory.class)
        );
        assertTrue(error.getMessage().contains("astratt"),
                "Il messaggio dovrebbe indicare che l'AbstractFactory non è astratta");
    }

    @Test
    void missingFactoryMethodInAbstractFactoryShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(SingleMethodFactory.class)
                        .implementsAbstractFactory()
                        .producing(Button.class, TextField.class)
                        .withConcreteFactory(LightThemeFactory.class)
        );
        assertTrue(error.getMessage().contains("TextField"),
                "Il messaggio dovrebbe indicare il factory method mancante per TextField");
    }

    @Test
    void wrongParentConcreteFactoryShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(UIFactory.class)
                        .implementsAbstractFactory()
                        .producing(Button.class, TextField.class)
                        .withConcreteFactory(WrongParentConcreteFactory.class)
        );
        assertTrue(error.getMessage().contains("implementa") || error.getMessage().contains("estende"),
                "Il messaggio dovrebbe indicare che il ConcreteFactory non implementa l'AbstractFactory");
    }

    @Test
    void missingMethodConcreteFactoryShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(UIFactory.class)
                        .implementsAbstractFactory()
                        .producing(Button.class, TextField.class)
                        .withConcreteFactory(MissingMethodConcreteFactory.class)
        );
        assertTrue(error.getMessage().contains("createTextField"),
                "Il messaggio dovrebbe indicare il factory method non implementato");
    }

    @Test
    void multipleViolationsShouldAllBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(AllViolationsAbstractFactory.class)
                        .implementsAbstractFactory()
                        .producing(Button.class, TextField.class)
                        .withConcreteFactory(AllViolationsConcreteFactory.class)
        );
        String msg = error.getMessage();
        assertTrue(msg.contains("astratt"),
                "Dovrebbe riportare che l'AbstractFactory non è astratta");
        assertTrue(msg.contains("TextField"),
                "Dovrebbe riportare il factory method mancante per TextField");
        assertTrue(msg.contains("implementa") || msg.contains("estende"),
                "Dovrebbe riportare che il ConcreteFactory non implementa l'AbstractFactory");
        assertTrue(msg.contains("createButton"),
                "Dovrebbe riportare il factory method non implementato nel ConcreteFactory");
    }
}
