package com.patternverifier.visitor;

import com.patternverifier.PatternAssertions;
import com.patternverifier.visitor.correct.AreaCalculator;
import com.patternverifier.visitor.correct.DocumentElement;
import com.patternverifier.visitor.correct.DocumentVisitor;
import com.patternverifier.visitor.correct.HtmlExporter;
import com.patternverifier.visitor.correct.Shape;
import com.patternverifier.visitor.correct.ShapeVisitor;
import com.patternverifier.visitor.wrong.AbstractConcreteVisitor;
import com.patternverifier.visitor.wrong.ConcreteVisitorInterface;
import com.patternverifier.visitor.wrong.NoAcceptElement;
import com.patternverifier.visitor.wrong.NoVisitMethodVisitor;
import com.patternverifier.visitor.wrong.WrongParentConcreteVisitor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VisitorVerifierTest {

    @Test
    void htmlExporterShouldPass() {
        PatternAssertions.assertThat(HtmlExporter.class)
                .implementsVisitor()
                .withVisitorInterface(DocumentVisitor.class)
                .withElement(DocumentElement.class);
    }

    @Test
    void areaCalculatorWithAbstractVisitorShouldPass() {
        // Variante: Visitor come classe astratta invece di interfaccia
        PatternAssertions.assertThat(AreaCalculator.class)
                .implementsVisitor()
                .withVisitorInterface(ShapeVisitor.class)
                .withElement(Shape.class);
    }

    @Test
    void concreteVisitorInterfaceShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(HtmlExporter.class)
                        .implementsVisitor()
                        .withVisitorInterface(ConcreteVisitorInterface.class)
                        .withElement(DocumentElement.class)
        );
        assertTrue(error.getMessage().contains("interfaccia") || error.getMessage().contains("astratt"),
                "Il messaggio dovrebbe indicare che il Visitor non è astratto");
    }

    @Test
    void noVisitMethodVisitorShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(HtmlExporter.class)
                        .implementsVisitor()
                        .withVisitorInterface(NoVisitMethodVisitor.class)
                        .withElement(DocumentElement.class)
        );
        assertTrue(error.getMessage().contains("visit"),
                "Il messaggio dovrebbe indicare la mancanza di metodi visit*");
    }

    @Test
    void noAcceptElementShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(HtmlExporter.class)
                        .implementsVisitor()
                        .withVisitorInterface(DocumentVisitor.class)
                        .withElement(NoAcceptElement.class)
        );
        assertTrue(error.getMessage().contains("accept") || error.getMessage().contains("dispatch"),
                "Il messaggio dovrebbe indicare la mancanza del metodo accept nell'Element");
    }

    @Test
    void wrongParentConcreteVisitorShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(WrongParentConcreteVisitor.class)
                        .implementsVisitor()
                        .withVisitorInterface(DocumentVisitor.class)
                        .withElement(DocumentElement.class)
        );
        assertTrue(error.getMessage().contains("implementa") || error.getMessage().contains("estende"),
                "Il messaggio dovrebbe indicare che il ConcreteVisitor non implementa Visitor");
    }

    @Test
    void abstractConcreteVisitorShouldBeReported() {
        // Java constraint: AbstractConcreteVisitor deve essere abstract perché
        // non implementa tutti i metodi di DocumentVisitor
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(AbstractConcreteVisitor.class)
                        .implementsVisitor()
                        .withVisitorInterface(DocumentVisitor.class)
                        .withElement(DocumentElement.class)
        );
        assertTrue(error.getMessage().contains("visit") || error.getMessage().contains("concreta"),
                "Il messaggio dovrebbe indicare la mancanza di un'implementazione concreta di visit*");
    }

    @Test
    void multipleViolationsShouldAllBeReported() {
        // WrongParentConcreteVisitor (check 4) + ConcreteVisitorInterface (check 1)
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(WrongParentConcreteVisitor.class)
                        .implementsVisitor()
                        .withVisitorInterface(ConcreteVisitorInterface.class)
                        .withElement(DocumentElement.class)
        );
        String msg = error.getMessage();
        assertTrue(msg.contains("interfaccia") || msg.contains("astratt"),
                "Dovrebbe riportare che il Visitor non è astratto");
        assertTrue(msg.contains("implementa") || msg.contains("estende"),
                "Dovrebbe riportare che il ConcreteVisitor non implementa Visitor");
    }
}
