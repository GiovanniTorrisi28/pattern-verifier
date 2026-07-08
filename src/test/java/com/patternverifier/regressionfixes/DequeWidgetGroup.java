package com.patternverifier.regressionfixes;

import java.util.ArrayDeque;

/**
 * Composite valido che mantiene i figli in un {@link ArrayDeque}.
 *
 * Regressione per il fix "COLLECTION_TYPES condiviso": prima della correzione
 * {@code ArrayDeque} era riconosciuto da ObserverVerifier ma NON da CompositeVerifier,
 * quindi questa classe falliva il controllo sul campo Collection pur essendo un Composite
 * strutturalmente corretto. Ora deve passare.
 */
public class DequeWidgetGroup implements Widget {

    private final ArrayDeque<Widget> children = new ArrayDeque<>();

    public void addChild(Widget child) {
        children.add(child);
    }

    @Override
    public void render() {
        for (Widget child : children) {
            child.render();
        }
    }
}
