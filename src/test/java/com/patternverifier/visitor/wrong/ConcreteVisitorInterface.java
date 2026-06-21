package com.patternverifier.visitor.wrong;

import com.patternverifier.visitor.correct.Paragraph;
import com.patternverifier.visitor.correct.Image;

// VIOLAZIONE 1: Visitor concreto — non è né interfaccia né classe astratta
public class ConcreteVisitorInterface {
    public void visitParagraph(Paragraph p) { System.out.println(p.getText()); }
    public void visitImage(Image i) { System.out.println(i.getSrc()); }
}
