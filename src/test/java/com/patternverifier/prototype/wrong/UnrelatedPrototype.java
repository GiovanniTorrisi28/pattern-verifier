package com.patternverifier.prototype.wrong;

// VIOLAZIONE: non si conforma al tipo Prototype dichiarato.
public class UnrelatedPrototype {
    public Object cloneIt() { return new UnrelatedPrototype(); }
}
