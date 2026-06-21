package com.patternverifier.bridge.correct;

// Variante: Implementor come classe astratta invece di interfaccia
public abstract class DrawingAPI {
    public abstract void drawCircle(double x, double y, double radius);
    public abstract void drawLine(double x1, double y1, double x2, double y2);
}
