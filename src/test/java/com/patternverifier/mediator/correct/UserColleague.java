package com.patternverifier.mediator.correct;

// Colleague: conosce solo il Mediator, nessun riferimento diretto agli altri partecipanti.
public class UserColleague implements Participant {
    private final ChatMediator mediator;
    private final String name;

    public UserColleague(ChatMediator mediator, String name) {
        this.mediator = mediator;
        this.name = name;
    }

    public void say(String message) {
        mediator.send(message, this);
    }

    @Override
    public void receive(String message) { }

    public String getName() { return name; }
}
