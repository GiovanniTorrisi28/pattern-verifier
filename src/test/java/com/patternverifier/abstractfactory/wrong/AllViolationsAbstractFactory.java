package com.patternverifier.abstractfactory.wrong;

import com.patternverifier.abstractfactory.correct.Button;

// Usato come AbstractFactory nel test delle violazioni multiple.
// VIOLAZIONE 1: non è né astratta né un'interfaccia
// VIOLAZIONE 2: ha solo 1 factory method — manca quello per TextField
public class AllViolationsAbstractFactory {
    public Button createButton() { return null; }
}
