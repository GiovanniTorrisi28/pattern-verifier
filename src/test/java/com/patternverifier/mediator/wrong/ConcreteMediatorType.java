package com.patternverifier.mediator.wrong;

import com.patternverifier.mediator.correct.Participant;

// VIOLAZIONE: il Mediator è una classe concreta — i Colleague sarebbero costretti a
// dipendere da un'implementazione specifica invece che da un contratto astratto.
public class ConcreteMediatorType {
    public void send(String message, Participant sender) { }
}
