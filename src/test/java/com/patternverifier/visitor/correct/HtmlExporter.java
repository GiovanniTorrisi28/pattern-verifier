package com.patternverifier.visitor.correct;

import com.patternverifier.annotations.GoFVisitor;

@GoFVisitor(visitorInterface = DocumentVisitor.class,
            element = DocumentElement.class,
            concreteElement = Paragraph.class)
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
