package com.patternverifier.visitor;

import com.patternverifier.PatternAssertions;
import com.patternverifier.visitor.correct.AreaCalculator;
import com.patternverifier.visitor.correct.DocumentElement;
import com.patternverifier.visitor.correct.DocumentVisitor;
import com.patternverifier.visitor.correct.HtmlExporter;
import com.patternverifier.visitor.correct.Image;
import com.patternverifier.visitor.correct.Paragraph;
import com.patternverifier.visitor.correct.Shape;
import com.patternverifier.visitor.correct.ShapeVisitor;
import com.patternverifier.visitor.wrong.AbstractConcreteVisitor;
import com.patternverifier.visitor.wrong.ConcreteVisitorInterface;
import com.patternverifier.visitor.wrong.NoAcceptElement;
import com.patternverifier.visitor.wrong.NoDispatchElement;
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
                .withElement(DocumentElement.class)
                .withoutConcreteElement();
    }

    @Test
    void areaCalculatorWithAbstractVisitorShouldPass() {
        // Variante: Visitor come classe astratta invece di interfaccia
        PatternAssertions.assertThat(AreaCalculator.class)
                .implementsVisitor()
                .withVisitorInterface(ShapeVisitor.class)
                .withElement(Shape.class)
                .withoutConcreteElement();
    }

    @Test
    void concreteVisitorInterfaceShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(HtmlExporter.class)
                        .implementsVisitor()
                        .withVisitorInterface(ConcreteVisitorInterface.class)
                        .withElement(DocumentElement.class)
                        .withoutConcreteElement()
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
                        .withoutConcreteElement()
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
                        .withoutConcreteElement()
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
                        .withoutConcreteElement()
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
                        .withoutConcreteElement()
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
                        .withoutConcreteElement()
        );
        String msg = error.getMessage();
        assertTrue(msg.contains("interfaccia") || msg.contains("astratt"),
                "Dovrebbe riportare che il Visitor non è astratto");
        assertTrue(msg.contains("implementa") || msg.contains("estende"),
                "Dovrebbe riportare che il ConcreteVisitor non implementa Visitor");
    }

    @Test
    void noDispatchElementShouldBeReported() {
        // Element passata direttamente come classe concreta: il double dispatch viene
        // verificato anche senza dichiarare una ConcreteElement.
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(HtmlExporter.class)
                        .implementsVisitor()
                        .withVisitorInterface(DocumentVisitor.class)
                        .withElement(NoDispatchElement.class)
                        .withoutConcreteElement()
        );
        assertTrue(error.getMessage().contains("invoca"),
                "Il messaggio dovrebbe indicare che l'Element non invoca metodi sul Visitor in accept");
    }

    // ---------------------------------------------------------------------
    // ConcreteElement esplicita — verifica del double dispatch nel caso canonico
    // (Element dichiarata come interfaccia).
    // ---------------------------------------------------------------------

    @Test
    void concreteElementWithProperDispatchShouldPass() {
        PatternAssertions.assertThat(HtmlExporter.class)
                .implementsVisitor()
                .withVisitorInterface(DocumentVisitor.class)
                .withElement(DocumentElement.class)
                .withConcreteElement(Paragraph.class);
    }

    @Test
    void anotherConcreteElementWithProperDispatchShouldPass() {
        PatternAssertions.assertThat(HtmlExporter.class)
                .implementsVisitor()
                .withVisitorInterface(DocumentVisitor.class)
                .withElement(DocumentElement.class)
                .withConcreteElement(Image.class);
    }

    @Test
    void concreteElementWithoutDispatchShouldBeReportedEvenWhenElementIsInterface() {
        // Test di regressione per la lacuna colmata: prima dell'introduzione di
        // withConcreteElement questo caso passava silenziosamente. DocumentElement è
        // un'interfaccia, quindi il controllo sul double dispatch veniva saltato e
        // NoDispatchElement (accept con corpo vuoto) non veniva mai esaminata — la proprietà
        // comportamentale centrale del Visitor restava di fatto non verificata nel caso canonico.
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(HtmlExporter.class)
                        .implementsVisitor()
                        .withVisitorInterface(DocumentVisitor.class)
                        .withElement(DocumentElement.class)
                        .withConcreteElement(NoDispatchElement.class)
        );
        assertTrue(error.getMessage().contains("invoca"),
                "Il messaggio dovrebbe indicare che la ConcreteElement non realizza il double dispatch");
    }

    @Test
    void concreteElementNotImplementingElementShouldBeReported() {
        // HtmlExporter è un Visitor, non un Element: non si conforma a DocumentElement.
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(HtmlExporter.class)
                        .implementsVisitor()
                        .withVisitorInterface(DocumentVisitor.class)
                        .withElement(DocumentElement.class)
                        .withConcreteElement(HtmlExporter.class)
        );
        assertTrue(error.getMessage().contains("implementa") || error.getMessage().contains("estende"),
                "Il messaggio dovrebbe indicare che la ConcreteElement non si conforma all'Element");
    }

    @Test
    void terminalMethodBeforeElementShouldFailFast() {
        assertThrows(IllegalStateException.class, () ->
                PatternAssertions.assertThat(HtmlExporter.class)
                        .implementsVisitor()
                        .withVisitorInterface(DocumentVisitor.class)
                        .withoutConcreteElement()
        );
    }
}
