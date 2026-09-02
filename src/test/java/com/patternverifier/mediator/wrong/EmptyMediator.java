package com.patternverifier.mediator.wrong;

import com.patternverifier.mediator.correct.ChatMediator;
import com.patternverifier.mediator.correct.Participant;

// VIOLAZIONE: implementa il contratto Mediator ma non mantiene alcun Colleague — nessun campo di
// tipo Participant, nessuna Collection di Participant, nessun metodo di registrazione. Un
// "mediatore" che non conosce i partecipanti che dovrebbe coordinare (GoF: "ConcreteMediator
// knows and maintains its colleagues").
public class EmptyMediator implements ChatMediator {
    private int messageCount;

    @Override
    public void send(String message, Participant sender) {
        messageCount++;
    }
}
