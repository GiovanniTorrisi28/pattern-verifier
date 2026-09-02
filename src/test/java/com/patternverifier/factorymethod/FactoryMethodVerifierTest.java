package com.patternverifier.factorymethod;

import com.patternverifier.PatternAssertions;
import com.patternverifier.factorymethod.batch.AnimalKennel;
import com.patternverifier.factorymethod.batch.DogKennel;
import com.patternverifier.factorymethod.batch.RawAnimalKennel;
import com.patternverifier.factorymethod.batch.RawDogKennel;
import com.patternverifier.factorymethod.correct.Animal;
import com.patternverifier.factorymethod.correct.AnimalCreator;
import com.patternverifier.factorymethod.correct.AnimalFactory;
import com.patternverifier.factorymethod.correct.BirdFactory;
import com.patternverifier.factorymethod.correct.CatFactory;
import com.patternverifier.factorymethod.correct.DogFactory;
import com.patternverifier.factorymethod.wrong.AllViolationsFactory;
import com.patternverifier.factorymethod.wrong.NoFactoryMethodCreator;
import com.patternverifier.factorymethod.wrong.NoOverrideFactory;
import com.patternverifier.factorymethod.wrong.NotAbstractCreator;
import com.patternverifier.factorymethod.wrong.WrongParentFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FactoryMethodVerifierTest {

    @Test
    void correctFactoryMethodShouldPass() {
        PatternAssertions.assertThat(AnimalFactory.class)
                .implementsFactoryMethod()
                .withAbstractFactoryMethod("createAnimal", Animal.class)
                .withConcreteCreator(DogFactory.class);
    }

    @Test
    void secondConcreteCreatorShouldAlsoPass() {
        // Variante: secondo ConcreteCreator sulla stessa gerarchia Creator
        PatternAssertions.assertThat(AnimalFactory.class)
                .implementsFactoryMethod()
                .withAbstractFactoryMethod("createAnimal", Animal.class)
                .withConcreteCreator(CatFactory.class);
    }

    @Test
    void interfaceCreatorShouldPass() {
        // Variante: Creator come interfaccia — ConcreteCreator usa implements invece di extends
        PatternAssertions.assertThat(AnimalCreator.class)
                .implementsFactoryMethod()
                .withAbstractFactoryMethod("createAnimal", Animal.class)
                .withConcreteCreator(BirdFactory.class);
    }

    @Test
    void notAbstractCreatorShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(NotAbstractCreator.class)
                        .implementsFactoryMethod()
                        .withAbstractFactoryMethod("createAnimal", Animal.class)
                        .withConcreteCreator(DogFactory.class)
        );
        assertTrue(error.getMessage().contains("astratt"),
                "Il messaggio dovrebbe indicare che il Creator non è astratto");
    }

    @Test
    void missingFactoryMethodInCreatorShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(NoFactoryMethodCreator.class)
                        .implementsFactoryMethod()
                        .withAbstractFactoryMethod("createAnimal", Animal.class)
                        .withConcreteCreator(DogFactory.class)
        );
        assertTrue(error.getMessage().contains("createAnimal"),
                "Il messaggio dovrebbe indicare il factory method mancante nel Creator");
    }

    @Test
    void wrongParentShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(AnimalFactory.class)
                        .implementsFactoryMethod()
                        .withAbstractFactoryMethod("createAnimal", Animal.class)
                        .withConcreteCreator(WrongParentFactory.class)
        );
        assertTrue(error.getMessage().contains("estende"),
                "Il messaggio dovrebbe indicare che il ConcreteCreator non estende il Creator");
    }

    @Test
    void missingOverrideShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(AnimalFactory.class)
                        .implementsFactoryMethod()
                        .withAbstractFactoryMethod("createAnimal", Animal.class)
                        .withConcreteCreator(NoOverrideFactory.class)
        );
        assertTrue(error.getMessage().contains("override"),
                "Il messaggio dovrebbe indicare che il ConcreteCreator non fa override del factory method");
    }

    @Test
    void multipleViolationsShouldAllBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(NotAbstractCreator.class)
                        .implementsFactoryMethod()
                        .withAbstractFactoryMethod("createAnimal", Animal.class)
                        .withConcreteCreator(AllViolationsFactory.class)
        );
        String msg = error.getMessage();
        assertTrue(msg.contains("astratt"),   "Dovrebbe riportare che il Creator non è astratto");
        assertTrue(msg.contains("createAnimal"), "Dovrebbe riportare il factory method mancante");
        assertTrue(msg.contains("estende"),   "Dovrebbe riportare che il ConcreteCreator non estende il Creator");
        assertTrue(msg.contains("override"),  "Dovrebbe riportare il factory method non implementato");
    }

    @Test
    void batchFactoryMethodReturningGenericCollectionShouldPass() {
        // Variante "a lotti": il factory method astratto restituisce List<Animal> anziché Animal
        // (analogo a Vector<Handle> handles() in JHotDraw, ma con generics dichiarati).
        PatternAssertions.assertThat(AnimalKennel.class)
                .implementsFactoryMethod()
                .withAbstractFactoryMethod("createAnimals", Animal.class)
                .withConcreteCreator(DogKennel.class);
    }

    @Test
    void batchFactoryMethodReturningRawCollectionShouldBeReported() {
        // Collezione raw (senza generics, come JHotDraw 1997): nessun attributo Signature da cui
        // leggere il tipo elemento, quindi il match non può essere confermato.
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(RawAnimalKennel.class)
                        .implementsFactoryMethod()
                        .withAbstractFactoryMethod("createAnimals", Animal.class)
                        .withConcreteCreator(RawDogKennel.class)
        );
        assertTrue(error.getMessage().contains("createAnimals"),
                "Il messaggio dovrebbe indicare il factory method non riconosciuto (collezione raw)");
    }
}
