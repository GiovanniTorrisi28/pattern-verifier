package com.patternverifier.abstractfactory.correct;

public class LightThemeFactory implements UIFactory {
    @Override
    public Button createButton() { return null; }
    @Override
    public TextField createTextField() { return null; }
}
