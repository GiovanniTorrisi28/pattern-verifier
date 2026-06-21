package com.patternverifier.observer.wrong;

// VIOLAZIONE 2: ha metodi ma nessuno rispetta la naming convention Observer
// (update*, on*, handle*) — il metodo process() non è riconosciuto
public interface WrongNamingObserver {
    void process(String event);
}
