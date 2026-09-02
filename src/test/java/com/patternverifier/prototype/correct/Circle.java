package com.patternverifier.prototype.correct;

import com.patternverifier.annotations.GoFPrototype;

// ConcretePrototype: sa produrre una copia di sé stesso.
@GoFPrototype(prototype = Shape.class, client = ShapeSpawner.class)
public class Circle implements Shape {
    private int radius;

    public Circle(int radius) { this.radius = radius; }

    @Override
    public Shape cloneShape() {
        return new Circle(this.radius);
    }

    @Override
    public void draw() { }
}
