package com.patternverifier.strategy.wrong;

import com.patternverifier.strategy.correct.SortStrategy;

// VIOLAZIONE 4: ha il campo ma né setter né costruttore che accetti Strategy —
// la Strategy non è intercambiabile dall'esterno
public class MissingInjectionContext {
    @SuppressWarnings("unused")
    private SortStrategy strategy;

    public void sort(int[] data) {}
}
