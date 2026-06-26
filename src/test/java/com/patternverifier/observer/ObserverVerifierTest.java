package com.patternverifier.observer;

import com.patternverifier.PatternAssertions;
import com.patternverifier.observer.correct.EventBus;
import com.patternverifier.observer.correct.EventListener;
import com.patternverifier.observer.correct.NewsListener;
import com.patternverifier.observer.correct.NewsPublisher;
import com.patternverifier.observer.wrong.AllViolationsSubject;
import com.patternverifier.observer.wrong.ConcreteObserver;
import com.patternverifier.observer.wrong.MissingCollectionSubject;
import com.patternverifier.observer.wrong.MissingNotifySubject;
import com.patternverifier.observer.wrong.MissingRegisterSubject;
import com.patternverifier.observer.wrong.EmptyNotifySubject;
import com.patternverifier.observer.wrong.WrongNamingObserver;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObserverVerifierTest {

    @Test
    void eventBusWithListenerShouldPass() {
        PatternAssertions.assertThat(EventBus.class)
                .implementsObserver()
                .withObserverInterface(EventListener.class);
    }

    @Test
    void newsPublisherWithSubscriberShouldPass() {
        // Variante: naming convention diversa (subscribe/fireUpdate) e Set invece di List
        PatternAssertions.assertThat(NewsPublisher.class)
                .implementsObserver()
                .withObserverInterface(NewsListener.class);
    }

    @Test
    void concreteObserverShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(EventBus.class)
                        .implementsObserver()
                        .withObserverInterface(ConcreteObserver.class)
        );
        assertTrue(error.getMessage().contains("astratt") || error.getMessage().contains("interfaccia"),
                "Il messaggio dovrebbe indicare che l'Observer non è astratto");
    }

    @Test
    void wrongNamingObserverShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(EventBus.class)
                        .implementsObserver()
                        .withObserverInterface(WrongNamingObserver.class)
        );
        assertTrue(error.getMessage().contains("naming") || error.getMessage().contains("update"),
                "Il messaggio dovrebbe indicare la naming convention mancante nell'Observer");
    }

    @Test
    void missingCollectionSubjectShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(MissingCollectionSubject.class)
                        .implementsObserver()
                        .withObserverInterface(EventListener.class)
        );
        assertTrue(error.getMessage().contains("Collection") || error.getMessage().contains("collezione"),
                "Il messaggio dovrebbe indicare il campo Collection mancante");
    }

    @Test
    void missingRegisterSubjectShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(MissingRegisterSubject.class)
                        .implementsObserver()
                        .withObserverInterface(EventListener.class)
        );
        assertTrue(error.getMessage().contains("register") || error.getMessage().contains("registrar"),
                "Il messaggio dovrebbe indicare il metodo di registrazione mancante");
    }

    @Test
    void missingNotifySubjectShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(MissingNotifySubject.class)
                        .implementsObserver()
                        .withObserverInterface(EventListener.class)
        );
        assertTrue(error.getMessage().contains("notif") || error.getMessage().contains("fire"),
                "Il messaggio dovrebbe indicare il metodo di notifica mancante");
    }

    @Test
    void multipleViolationsShouldAllBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(AllViolationsSubject.class)
                        .implementsObserver()
                        .withObserverInterface(ConcreteObserver.class)
        );
        String msg = error.getMessage();
        assertTrue(msg.contains("astratt") || msg.contains("interfaccia"),
                "Dovrebbe riportare che l'Observer non è astratto");
        assertTrue(msg.contains("Collection") || msg.contains("collezione"),
                "Dovrebbe riportare il campo Collection mancante");
        assertTrue(msg.contains("register") || msg.contains("registrar"),
                "Dovrebbe riportare il metodo di registrazione mancante");
        assertTrue(msg.contains("notif") || msg.contains("fire"),
                "Dovrebbe riportare il metodo di notifica mancante");
        assertTrue(msg.contains("invoca"),
                "Dovrebbe riportare che il Subject non invoca metodi sugli Observer");
    }

    @Test
    void emptyNotifySubjectShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(EmptyNotifySubject.class)
                        .implementsObserver()
                        .withObserverInterface(EventListener.class)
        );
        assertTrue(error.getMessage().contains("invoca"),
                "Il messaggio dovrebbe indicare che il Subject non invoca metodi sugli Observer");
    }
}
