package com.patternverifier.abstractfactory.correct;

public class DarkThemeFactory extends AbstractUIFactory {
    @Override
    public Button createButton() { return null; }
    @Override
    public TextField createTextField() { return null; }
}
