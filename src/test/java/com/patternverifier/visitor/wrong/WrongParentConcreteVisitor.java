package com.patternverifier.visitor.wrong;

import com.patternverifier.visitor.correct.Paragraph;
import com.patternverifier.visitor.correct.Image;

// VIOLAZIONE 4: ConcreteVisitor che non implementa l'interfaccia Visitor dichiarata
// Ha metodi visit* ma non è collegato alla gerarchia Visitor
public class WrongParentConcreteVisitor {
    public void visitParagraph(Paragraph p) { System.out.println(p.getText()); }
    public void visitImage(Image i) { System.out.println(i.getSrc()); }
}
