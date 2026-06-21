package com.patternverifier.visitor.wrong;

import com.patternverifier.visitor.correct.DocumentVisitor;

// VIOLAZIONE 3: Element senza metodo accept(Visitor)
// Senza accept il double dispatch non è possibile
public interface NoAcceptElement {
    void render();
    void serialize(DocumentVisitor visitor);
}
