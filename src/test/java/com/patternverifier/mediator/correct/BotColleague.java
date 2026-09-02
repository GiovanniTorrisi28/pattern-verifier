package com.patternverifier.mediator.correct;

// Secondo Colleague: anch'esso conosce solo il Mediator.
public class BotColleague implements Participant {
    private final ChatMediator mediator;

    public BotColleague(ChatMediator mediator) { this.mediator = mediator; }

    public void announce(String message) {
        mediator.send(message, this);
    }

    @Override
    public void receive(String message) { }
}
