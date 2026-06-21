package com.patternverifier.visitor.correct;

// Variante: Visitor come classe astratta invece di interfaccia
public abstract class ShapeVisitor {
    public abstract void visitCircle(Circle circle);
    public abstract void visitRectangle(Rectangle rectangle);
}
