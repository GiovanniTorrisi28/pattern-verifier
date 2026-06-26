package com.patternverifier.strategy.wrong;

import com.patternverifier.strategy.correct.SortStrategy;

// Violazione: struttura corretta (campo + setter) ma non invoca mai metodi su SortStrategy.
public class NoStrategyInvocationContext {

    @SuppressWarnings("unused")
    private SortStrategy strategy;

    public void setStrategy(SortStrategy strategy) {
        this.strategy = strategy;
    }

    public void sort(int[] data) {
        // non chiama mai strategy.sort() o altri metodi su SortStrategy
    }
}
