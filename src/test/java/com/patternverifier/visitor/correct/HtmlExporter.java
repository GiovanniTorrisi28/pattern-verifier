package com.patternverifier.visitor.correct;

public class HtmlExporter implements DocumentVisitor {

    @Override
    public void visitParagraph(Paragraph paragraph) {
        System.out.println("<p>" + paragraph.getText() + "</p>");
    }

    @Override
    public void visitImage(Image image) {
        System.out.println("<img src=\"" + image.getSrc() + "\">");
    }
}
