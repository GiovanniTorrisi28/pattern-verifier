package com.patternverifier.strategy.correct;

import com.patternverifier.annotations.GoFStrategy;

@GoFStrategy(strategy = SortStrategy.class)
public class SorterWithSetter {
    private SortStrategy strategy;

    public void setStrategy(SortStrategy strategy) {
        this.strategy = strategy;
    }

    public void sort(int[] data) {
        strategy.sort(data);
    }
}
