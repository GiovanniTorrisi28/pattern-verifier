package com.patternverifier.prototype.correct;

// Variante: ConcretePrototype che usa il naming "copy*" invece di "clone*".
public class Square implements Shape {
    private int side;

    public Square(int side) { this.side = side; }

    @Override
    public Shape cloneShape() { return copyOf(); }

    public Square copyOf() { return new Square(this.side); }

    @Override
    public void draw() { }
}
