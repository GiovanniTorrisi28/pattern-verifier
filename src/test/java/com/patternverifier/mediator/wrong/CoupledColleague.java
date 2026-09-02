package com.patternverifier.mediator.wrong;

import com.patternverifier.mediator.correct.BotColleague;
import com.patternverifier.mediator.correct.ChatMediator;
import com.patternverifier.mediator.correct.Participant;

// VIOLAZIONE: conosce il Mediator ma detiene ANCHE un riferimento diretto a un altro
// Colleague — è esattamente l'accoppiamento che il Mediator deve eliminare.
public class CoupledColleague implements Participant {
    private final ChatMediator mediator;
    private final BotColleague peer;

    public CoupledColleague(ChatMediator mediator, BotColleague peer) {
        this.mediator = mediator;
        this.peer = peer;
    }

    @Override
    public void receive(String message) { }
}
