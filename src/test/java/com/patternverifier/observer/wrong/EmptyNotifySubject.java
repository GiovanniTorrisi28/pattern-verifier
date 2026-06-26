package com.patternverifier.observer.wrong;

import com.patternverifier.observer.correct.EventListener;

import java.util.ArrayList;
import java.util.List;

// Violazione: struttura corretta (Collection + register + notify) ma il metodo notify
// non invoca mai metodi sugli observer — notifica vuota.
public class EmptyNotifySubject {

    private List<EventListener> listeners = new ArrayList<>();

    public void addEventListener(EventListener listener) {
        listeners.add(listener);
    }

    public void notifyListeners(String event) {
        // non chiama mai l.onEvent() o altri metodi su EventListener
    }
}
