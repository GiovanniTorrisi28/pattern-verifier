package com.patternverifier.visitor.correct;

public class Paragraph implements DocumentElement {
    private String text;

    public Paragraph(String text) { this.text = text; }
    public String getText() { return text; }

    @Override
    public void accept(DocumentVisitor visitor) {
        visitor.visitParagraph(this);
    }
}
