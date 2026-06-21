package com.patternverifier.templatemethod.correct;

public abstract class BeverageMaker {

    public void prepare() {
        boilWater();
        brew();
        pourInCup();
        addCondiments();
    }

    private void boilWater() {
        System.out.println("Acqua in ebollizione");
    }

    private void pourInCup() {
        System.out.println("Versare nella tazza");
    }

    protected abstract void brew();
    protected abstract void addCondiments();
}
