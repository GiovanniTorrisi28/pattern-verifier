package com.patternverifier.observer.correct;

import com.patternverifier.annotations.GoFObserver;
import java.util.ArrayList;
import java.util.List;

@GoFObserver(observer = EventListener.class)
public class EventBus {
    private List<EventListener> listeners = new ArrayList<>();

    public void addEventListener(EventListener listener) {
        listeners.add(listener);
    }

    public void removeEventListener(EventListener listener) {
        listeners.remove(listener);
    }

    public void notifyListeners(String event) {
        listeners.forEach(l -> l.onEvent(event));
    }
}
