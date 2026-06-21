package com.patternverifier.observer.correct;

import java.util.HashSet;
import java.util.Set;

// Variante: naming convention diversa (subscribe/fireUpdate) e Set invece di List
public class NewsPublisher {
    private Set<NewsListener> subscribers = new HashSet<>();

    public void subscribe(NewsListener listener) {
        subscribers.add(listener);
    }

    public void unsubscribe(NewsListener listener) {
        subscribers.remove(listener);
    }

    public void fireUpdate(String news) {
        subscribers.forEach(s -> s.handleNews(news));
    }
}
