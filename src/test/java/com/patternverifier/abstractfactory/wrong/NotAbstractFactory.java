package com.patternverifier.abstractfactory.wrong;

import com.patternverifier.abstractfactory.correct.Button;
import com.patternverifier.abstractfactory.correct.TextField;

// VIOLAZIONE 1: non è né astratta né un'interfaccia
public class NotAbstractFactory {
    public Button createButton() { return null; }
    public TextField createTextField() { return null; }
}
