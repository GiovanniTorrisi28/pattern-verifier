package com.patternverifier.factorymethod.wrong;

import com.patternverifier.factorymethod.correct.AnimalFactory;

// VIOLAZIONE: estende AnimalFactory ma non fa override del factory method.
// Deve essere abstract altrimenti il compilatore Java rifiuta la classe
// (non si può avere una classe concreta con metodi astratti non implementati).
public abstract class NoOverrideFactory extends AnimalFactory {
    public void doSomethingElse() {}
}
