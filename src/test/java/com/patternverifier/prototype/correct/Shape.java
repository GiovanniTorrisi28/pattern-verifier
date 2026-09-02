package com.patternverifier.prototype.correct;

// Prototype: dichiara il contratto di clonazione da cui il Client dipende.
public interface Shape extends Cloneable {
    Shape cloneShape();
    void draw();
}
