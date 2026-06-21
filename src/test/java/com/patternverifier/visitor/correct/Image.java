package com.patternverifier.visitor.correct;

public class Image implements DocumentElement {
    private String src;

    public Image(String src) { this.src = src; }
    public String getSrc() { return src; }

    @Override
    public void accept(DocumentVisitor visitor) {
        visitor.visitImage(this);
    }
}
