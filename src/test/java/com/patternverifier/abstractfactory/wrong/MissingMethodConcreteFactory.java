package com.patternverifier.abstractfactory.wrong;

import com.patternverifier.abstractfactory.correct.Button;
import com.patternverifier.abstractfactory.correct.UIFactory;

// VIOLAZIONE 4: implementa UIFactory ma non fa override di createTextField().
// Deve essere abstract: Java non consente classi concrete che implementano
// un'interfaccia senza implementare tutti i metodi astratti.
public abstract class MissingMethodConcreteFactory implements UIFactory {
    @Override
    public Button createButton() { return null; }
    // createTextField() rimane astratto
}
