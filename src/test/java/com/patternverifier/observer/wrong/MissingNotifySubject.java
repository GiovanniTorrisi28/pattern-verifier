package com.patternverifier.observer.wrong;

import com.patternverifier.observer.correct.EventListener;

import java.util.ArrayList;
import java.util.List;

// VIOLAZIONE 5: ha la collezione e il metodo register ma non ha un metodo
// di notifica (notify*/fire*/dispatch*)
public class MissingNotifySubject {
    @SuppressWarnings("unused")
    private List<EventListener> listeners = new ArrayList<>();

    public void addEventListener(EventListener listener) {
        listeners.add(listener);
    }
}
