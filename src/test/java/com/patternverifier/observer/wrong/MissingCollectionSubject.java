package com.patternverifier.observer.wrong;

import com.patternverifier.observer.correct.EventListener;

// VIOLAZIONE 3: non ha un campo Collection per mantenere gli observer
public class MissingCollectionSubject {
    public void addEventListener(EventListener listener) {}
    public void notifyListeners() {}
}
