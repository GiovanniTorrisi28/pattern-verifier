package com.patternverifier.visitor.wrong;

import com.patternverifier.visitor.correct.DocumentVisitor;

// VIOLAZIONE 5: ConcreteVisitor che implementa Visitor ma rimane abstract
// Java constraint: non implementa tutti i metodi di DocumentVisitor,
// quindi deve essere abstract — non ha visit* concreti propri.
public abstract class AbstractConcreteVisitor implements DocumentVisitor {
    // visitParagraph e visitImage rimangono abstract per eredità da DocumentVisitor
    // — questo è esattamente ciò che il check 5 vuole rilevare
}
