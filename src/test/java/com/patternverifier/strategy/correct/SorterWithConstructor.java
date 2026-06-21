package com.patternverifier.strategy.correct;

// Variante: Strategy iniettata via costruttore invece di setter
public class SorterWithConstructor {
    private SortStrategy strategy;

    public SorterWithConstructor(SortStrategy strategy) {
        this.strategy = strategy;
    }

    public void sort(int[] data) {
        strategy.sort(data);
    }
}
