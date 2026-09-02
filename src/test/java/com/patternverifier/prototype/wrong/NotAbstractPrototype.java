package com.patternverifier.prototype.wrong;

// VIOLAZIONE: il Prototype è una classe concreta — il Client sarebbe costretto a
// dipendere da un tipo concreto, vanificando lo scopo del pattern.
public class NotAbstractPrototype {
    public NotAbstractPrototype cloneIt() { return new NotAbstractPrototype(); }
}
