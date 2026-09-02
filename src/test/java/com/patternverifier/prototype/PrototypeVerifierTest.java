package com.patternverifier.prototype;

import com.patternverifier.PatternAssertions;
import com.patternverifier.prototype.correct.Circle;
import com.patternverifier.prototype.correct.Shape;
import com.patternverifier.prototype.correct.ShapeSpawner;
import com.patternverifier.prototype.correct.Square;
import com.patternverifier.prototype.wrong.InstantiatingClient;
import com.patternverifier.prototype.wrong.NoCloneMethodShape;
import com.patternverifier.prototype.wrong.NoPrototypeFieldClient;
import com.patternverifier.prototype.wrong.NotAbstractPrototype;
import com.patternverifier.prototype.wrong.UnrelatedPrototype;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PrototypeVerifierTest {

    @Test
    void circleWithSpawnerShouldPass() {
        PatternAssertions.assertThat(Circle.class)
                .implementsPrototype()
                .withPrototype(Shape.class)
                .withClient(ShapeSpawner.class);
    }

    @Test
    void squareWithCopyNamingShouldPass() {
        // Variante: il ConcretePrototype espone anche un metodo copy* oltre a cloneShape()
        PatternAssertions.assertThat(Square.class)
                .implementsPrototype()
                .withPrototype(Shape.class)
                .withClient(ShapeSpawner.class);
    }

    @Test
    void withoutClientShouldVerifyOnlyPrototypeSide() {
        PatternAssertions.assertThat(Circle.class)
                .implementsPrototype()
                .withPrototype(Shape.class)
                .withoutClient();
    }

    @Test
    void concretePrototypeTypeShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(Circle.class)
                        .implementsPrototype()
                        .withPrototype(NotAbstractPrototype.class)
                        .withoutClient()
        );
        assertTrue(error.getMessage().contains("interfaccia") || error.getMessage().contains("astratt"),
                "Il messaggio dovrebbe indicare che il Prototype non è astratto");
    }

    @Test
    void prototypeWithoutCloneMethodShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(Circle.class)
                        .implementsPrototype()
                        .withPrototype(NoCloneMethodShape.class)
                        .withoutClient()
        );
        assertTrue(error.getMessage().contains("clonazione"),
                "Il messaggio dovrebbe indicare la mancanza di un metodo di clonazione");
    }

    @Test
    void concretePrototypeNotConformingShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(UnrelatedPrototype.class)
                        .implementsPrototype()
                        .withPrototype(Shape.class)
                        .withoutClient()
        );
        assertTrue(error.getMessage().contains("implementa") || error.getMessage().contains("estende"),
                "Il messaggio dovrebbe indicare che il ConcretePrototype non si conforma al Prototype");
    }

    @Test
    void clientThatInstantiatesInsteadOfCloningShouldBeReported() {
        // Il Client detiene il prototipo ma non lo invoca mai: è la proprietà di Livello 2
        // che distingue un vero Prototype da una classe che tiene un esemplare inutilizzato.
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(Circle.class)
                        .implementsPrototype()
                        .withPrototype(Shape.class)
                        .withClient(InstantiatingClient.class)
        );
        assertTrue(error.getMessage().contains("non invoca mai"),
                "Il messaggio dovrebbe indicare che il Client non clona il prototipo");
    }

    @Test
    void clientWithoutPrototypeFieldShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(Circle.class)
                        .implementsPrototype()
                        .withPrototype(Shape.class)
                        .withClient(NoPrototypeFieldClient.class)
        );
        assertTrue(error.getMessage().contains("campo"),
                "Il messaggio dovrebbe indicare che il Client non conserva il prototipo");
    }

    @Test
    void multipleViolationsShouldAllBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(UnrelatedPrototype.class)
                        .implementsPrototype()
                        .withPrototype(NoCloneMethodShape.class)
                        .withClient(NoPrototypeFieldClient.class)
        );
        String msg = error.getMessage();
        assertTrue(msg.contains("clonazione"), "Dovrebbe riportare il Prototype senza metodo di clonazione");
        assertTrue(msg.contains("implementa") || msg.contains("estende"),
                "Dovrebbe riportare il ConcretePrototype non conforme");
        assertTrue(msg.contains("campo"), "Dovrebbe riportare il Client senza campo prototipale");
    }

    @Test
    void terminalMethodBeforePrototypeShouldFailFast() {
        assertThrows(IllegalStateException.class, () ->
                PatternAssertions.assertThat(Circle.class)
                        .implementsPrototype()
                        .withoutClient()
        );
    }
}
