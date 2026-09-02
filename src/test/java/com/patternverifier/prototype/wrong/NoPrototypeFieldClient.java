package com.patternverifier.prototype.wrong;

import com.patternverifier.prototype.correct.Circle;
import com.patternverifier.prototype.correct.Shape;

// VIOLAZIONE: il Client non conserva alcun esemplare prototipale.
public class NoPrototypeFieldClient {
    public Shape spawn() {
        return new Circle(1);
    }
}
