package com.patternverifier.abstractfactory.correct;

import com.patternverifier.annotations.GoFAbstractFactory;

@GoFAbstractFactory(abstractFactory = AbstractUIFactory.class, products = {Button.class, TextField.class})
public class DarkThemeFactory extends AbstractUIFactory {
    @Override
    public Button createButton() { return null; }
    @Override
    public TextField createTextField() { return null; }
}
