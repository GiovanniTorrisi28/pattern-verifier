package com.patternverifier.mediator.correct;

// Tipo comune dei Colleague. Ogni Colleague concreto dipende dal Mediator, non dagli altri.
public interface Participant {
    void receive(String message);
}
