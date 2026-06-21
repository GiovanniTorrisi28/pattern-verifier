package com.patternverifier.visitor.correct;

public interface DocumentElement {
    void accept(DocumentVisitor visitor);
}
