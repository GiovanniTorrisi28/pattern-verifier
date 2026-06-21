package com.patternverifier.observer.wrong;

import com.patternverifier.observer.correct.EventListener;

import java.util.ArrayList;
import java.util.List;

// VIOLAZIONE 4: ha la collezione e il metodo notify ma non ha un metodo
// per registrare observer (add*/register*/subscribe*)
public class MissingRegisterSubject {
    @SuppressWarnings("unused")
    private List<EventListener> listeners = new ArrayList<>();

    public void notifyListeners() {}
}
