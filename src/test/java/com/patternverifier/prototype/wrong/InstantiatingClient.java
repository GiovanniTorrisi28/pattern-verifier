package com.patternverifier.prototype.wrong;

import com.patternverifier.prototype.correct.Circle;
import com.patternverifier.prototype.correct.Shape;

// VIOLAZIONE: il Client detiene il prototipo ma non lo usa mai — crea le istanze
// con "new" su una classe concreta, che è esattamente ciò che il Prototype evita.
public class InstantiatingClient {
    private final Shape prototype;

    public InstantiatingClient(Shape prototype) { this.prototype = prototype; }

    public Shape spawn() {
        return new Circle(1);
    }
}
