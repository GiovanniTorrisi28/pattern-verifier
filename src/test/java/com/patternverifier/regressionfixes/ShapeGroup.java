package com.patternverifier.regressionfixes;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite valido il cui Component ({@link AbstractShape}) è una <b>classe astratta</b>, non
 * un'interfaccia — la relazione è quindi {@code extends}, non {@code implements}.
 *
 * Regressione per il fix "conformità a supertipo classe astratta": prima della correzione
 * {@code checkCompositeImplementsComponent} controllava solo {@code getInterfaces().contains(...)}
 * e falliva qui, pur essendo un Composite GoF valido. Ora deve passare.
 */
public class ShapeGroup extends AbstractShape {

    private final List<AbstractShape> children = new ArrayList<>();

    public void addChild(AbstractShape child) {
        children.add(child);
    }

    @Override
    public void draw() {
        for (AbstractShape child : children) {
            child.draw();
        }
    }
}
