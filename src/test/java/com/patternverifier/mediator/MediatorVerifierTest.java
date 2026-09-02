package com.patternverifier.mediator;

import com.patternverifier.PatternAssertions;
import com.patternverifier.mediator.correct.BotColleague;
import com.patternverifier.mediator.correct.ChatMediator;
import com.patternverifier.mediator.correct.ChatRoom;
import com.patternverifier.mediator.correct.UserColleague;
import com.patternverifier.mediator.wrong.ConcreteMediatorType;
import com.patternverifier.mediator.wrong.CoupledColleague;
import com.patternverifier.mediator.wrong.DetachedColleague;
import com.patternverifier.mediator.wrong.EmptyMediator;
import com.patternverifier.mediator.wrong.UnrelatedMediator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MediatorVerifierTest {

    @Test
    void chatRoomShouldPass() {
        PatternAssertions.assertThat(ChatRoom.class)
                .implementsMediator()
                .withMediatorInterface(ChatMediator.class)
                .withColleagues(UserColleague.class, BotColleague.class);
    }

    @Test
    void coupledColleagueShouldBeReported() {
        // Il caso centrale del pattern: un Colleague che detiene un riferimento diretto a un
        // altro Colleague. È una proprietà NEGATIVA — si verifica che qualcosa sia assente —
        // e diventa decidibile solo perché l'insieme dei Colleague è dichiarato esplicitamente.
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(ChatRoom.class)
                        .implementsMediator()
                        .withMediatorInterface(ChatMediator.class)
                        .withColleagues(CoupledColleague.class, BotColleague.class)
        );
        assertTrue(error.getMessage().contains("altro Colleague"),
                "Il messaggio dovrebbe indicare l'accoppiamento diretto tra Colleague");
    }

    @Test
    void colleagueWithoutMediatorFieldShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(ChatRoom.class)
                        .implementsMediator()
                        .withMediatorInterface(ChatMediator.class)
                        .withColleagues(DetachedColleague.class, BotColleague.class)
        );
        assertTrue(error.getMessage().contains("non ha un campo di tipo"),
                "Il messaggio dovrebbe indicare che il Colleague non conosce il Mediator");
    }

    @Test
    void concreteMediatorTypeShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(ChatRoom.class)
                        .implementsMediator()
                        .withMediatorInterface(ConcreteMediatorType.class)
                        .withColleagues(UserColleague.class, BotColleague.class)
        );
        assertTrue(error.getMessage().contains("interfaccia") || error.getMessage().contains("astratt"),
                "Il messaggio dovrebbe indicare che il Mediator non è astratto");
    }

    @Test
    void concreteMediatorNotConformingShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(UnrelatedMediator.class)
                        .implementsMediator()
                        .withMediatorInterface(ChatMediator.class)
                        .withColleagues(UserColleague.class, BotColleague.class)
        );
        assertTrue(error.getMessage().contains("implementa") || error.getMessage().contains("estende"),
                "Il messaggio dovrebbe indicare che il ConcreteMediator non realizza il Mediator");
    }

    @Test
    void mediatorNotMaintainingColleaguesShouldBeReported() {
        // GoF: "ConcreteMediator knows and maintains its colleagues". EmptyMediator implementa il
        // contratto ma non tiene alcun riferimento ai partecipanti.
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(EmptyMediator.class)
                        .implementsMediator()
                        .withMediatorInterface(ChatMediator.class)
                        .withColleagues(UserColleague.class, BotColleague.class)
        );
        assertTrue(error.getMessage().contains("non mantiene alcun riferimento"),
                "Il messaggio dovrebbe indicare che il ConcreteMediator non conosce i Colleague");
    }

    @Test
    void singleColleagueShouldBeReported() {
        // Con un solo Colleague non c'è alcuna interazione da mediare: la proprietà di
        // disaccoppiamento sarebbe vacuamente vera.
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(ChatRoom.class)
                        .implementsMediator()
                        .withMediatorInterface(ChatMediator.class)
                        .withColleagues(UserColleague.class)
        );
        assertTrue(error.getMessage().contains("almeno 2"),
                "Il messaggio dovrebbe indicare che servono almeno 2 Colleague");
    }

    @Test
    void multipleViolationsShouldAllBeReported() {
        // Nota: BotColleague va incluso nell'insieme dichiarato perché è il tipo del campo
        // che rende CoupledColleague accoppiato. La proprietà negativa è verificata sul solo
        // dominio dichiarato dal programmatore — un riferimento a una classe non dichiarata
        // come Colleague non è, per definizione, un accoppiamento tra Colleague.
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(UnrelatedMediator.class)
                        .implementsMediator()
                        .withMediatorInterface(ChatMediator.class)
                        .withColleagues(CoupledColleague.class, BotColleague.class, DetachedColleague.class)
        );
        String msg = error.getMessage();
        assertTrue(msg.contains("implementa") || msg.contains("estende"),
                "Dovrebbe riportare il ConcreteMediator non conforme");
        assertTrue(msg.contains("altro Colleague"), "Dovrebbe riportare l'accoppiamento diretto");
        assertTrue(msg.contains("non ha un campo di tipo"),
                "Dovrebbe riportare il Colleague privo di riferimento al Mediator");
    }

    @Test
    void colleaguesDeclaredBeforeMediatorInterfaceShouldFailFast() {
        assertThrows(IllegalStateException.class, () ->
                PatternAssertions.assertThat(ChatRoom.class)
                        .implementsMediator()
                        .withColleagues(UserColleague.class, BotColleague.class)
        );
    }
}
