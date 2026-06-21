package com.patternverifier.bridge.correct;

public class CircleShape {

    protected DrawingAPI drawingAPI;
    private double x, y, radius;

    public CircleShape(double x, double y, double radius, DrawingAPI drawingAPI) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.drawingAPI = drawingAPI;
    }

    public void draw() {
        drawingAPI.drawCircle(x, y, radius);
    }
}
