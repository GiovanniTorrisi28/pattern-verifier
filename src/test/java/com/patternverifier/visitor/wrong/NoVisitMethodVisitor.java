package com.patternverifier.visitor.wrong;

// VIOLAZIONE 2: Visitor astratto senza metodi visit*
// Non rispetta la naming convention del pattern
public interface NoVisitMethodVisitor {
    void process(Object element);
    void render(Object element);
}
