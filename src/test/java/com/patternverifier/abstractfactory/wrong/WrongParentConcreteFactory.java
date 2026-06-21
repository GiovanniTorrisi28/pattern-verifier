package com.patternverifier.abstractfactory.wrong;

import com.patternverifier.abstractfactory.correct.Button;
import com.patternverifier.abstractfactory.correct.TextField;

// VIOLAZIONE 3: non implementa UIFactory
public class WrongParentConcreteFactory {
    public Button createButton() { return null; }
    public TextField createTextField() { return null; }
}
