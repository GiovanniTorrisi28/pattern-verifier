package com.patternverifier.visitor.correct;

public interface DocumentVisitor {
    void visitParagraph(Paragraph paragraph);
    void visitImage(Image image);
}
