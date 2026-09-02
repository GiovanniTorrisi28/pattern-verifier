package com.patternverifier.prototype.correct;

// Client: detiene l'esemplare prototipale e crea nuove istanze clonandolo,
// senza conoscere la classe concreta.
public class ShapeSpawner {
    private final Shape prototype;

    public ShapeSpawner(Shape prototype) { this.prototype = prototype; }

    public Shape spawn() {
        return prototype.cloneShape();
    }
}
