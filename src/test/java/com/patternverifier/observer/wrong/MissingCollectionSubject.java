package com.patternverifier.observer.wrong;

import com.patternverifier.annotations.GoFObserver;
import com.patternverifier.observer.correct.EventListener;

// VIOLAZIONE 3: non ha un campo Collection per mantenere gli observer
@GoFObserver(observer = EventListener.class)
public class MissingCollectionSubject {
    public void addEventListener(EventListener listener) {}
    public void notifyListeners() {}
}
