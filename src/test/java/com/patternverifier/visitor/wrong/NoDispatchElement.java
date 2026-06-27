package com.patternverifier.visitor.wrong;

import com.patternverifier.visitor.correct.DocumentElement;
import com.patternverifier.visitor.correct.DocumentVisitor;

public class NoDispatchElement implements DocumentElement {

    @Override
    public void accept(DocumentVisitor visitor) {
        // non chiama visitor.visit(this) — il double dispatch non avviene
    }
}
