package com.patternverifier.mediator.correct;

// Mediator: incapsula il modo in cui i partecipanti interagiscono.
public interface ChatMediator {
    void send(String message, Participant sender);
}
