package com.patternverifier.chainofresponsibility.correct;

// Variante: interfaccia invece di classe astratta.
// Il campo self-reference è gestito dalla classe concreta, ma la firma
// del metodo setNext nell'interfaccia dichiara il tipo Handler.
public interface LogHandler {

    LogHandler next = null;

    void handle(String message);
}
