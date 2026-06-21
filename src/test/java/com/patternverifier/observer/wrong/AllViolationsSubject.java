package com.patternverifier.observer.wrong;

// Usato come Subject nel test delle violazioni multiple.
// VIOLAZIONE 3: non ha campo Collection
// VIOLAZIONE 4: non ha metodo register
// VIOLAZIONE 5: non ha metodo notify
public class AllViolationsSubject {
    public void doSomething() {}
}
