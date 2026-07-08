package com.patternverifier.regressionfixes;

/**
 * Component realizzato come <b>classe astratta</b> (non interfaccia) — configurazione comune
 * per Composite e Decorator nel testo GoF. Serve alla fixture {@link ShapeGroup}.
 */
public abstract class AbstractShape {
    public abstract void draw();
}
