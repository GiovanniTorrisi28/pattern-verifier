package com.patternverifier.mediator.wrong;

import com.patternverifier.mediator.correct.Participant;

// VIOLAZIONE: non ha alcun campo di tipo Mediator — non può comunicare attraverso di esso.
public class DetachedColleague implements Participant {
    private String name;

    @Override
    public void receive(String message) { }

    public String getName() { return name; }
}
