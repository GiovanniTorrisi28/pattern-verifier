package com.patternverifier.visitor.correct;

public class AreaCalculator extends ShapeVisitor {

    @Override
    public void visitCircle(Circle circle) {
        System.out.println("Area cerchio: " + Math.PI * circle.getRadius() * circle.getRadius());
    }

    @Override
    public void visitRectangle(Rectangle rectangle) {
        System.out.println("Area rettangolo calcolata");
    }
}
