package com.patternverifier.visitor.correct;

public interface Shape {
    void accept(ShapeVisitor visitor);
}
