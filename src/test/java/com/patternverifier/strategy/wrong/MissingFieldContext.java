package com.patternverifier.strategy.wrong;

import com.patternverifier.strategy.correct.SortStrategy;

// VIOLAZIONE 3: ha il setter ma non il campo — il Context deve mantenere
// un riferimento alla Strategy corrente
public class MissingFieldContext {
    public void setStrategy(SortStrategy strategy) {}

    public void sort(int[] data) {}
}
