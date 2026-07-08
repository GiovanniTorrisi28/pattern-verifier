package com.patternverifier.regressionfixes;

import com.patternverifier.PatternAnnotationScanner;
import com.patternverifier.PatternAssertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test di regressione per i tre fix di robustezza applicati dopo l'analisi del codice del tool
 * (vedi {@code docs/decisioni.md}). Ognuno di questi test <b>fallirebbe</b> senza il fix
 * corrispondente — la suite preesistente non li esercitava, quindi restava verde nonostante le
 * lacune.
 */
class VerifierRobustnessFixesTest {

    /**
     * Fix 1 — COLLECTION_TYPES condiviso: un Composite che usa {@link java.util.ArrayDeque} per
     * i figli deve passare. Prima del fix ArrayDeque era riconosciuto solo da ObserverVerifier.
     */
    @Test
    void compositeWithArrayDequeShouldPass() {
        assertDoesNotThrow(() ->
                PatternAssertions.assertThat(DequeWidgetGroup.class)
                        .implementsComposite()
                        .forComponent(Widget.class));
    }

    /**
     * Fix 3 — conformità a supertipo classe astratta: un Composite il cui Component è una classe
     * astratta (relazione {@code extends}, non {@code implements}) deve passare. Prima del fix il
     * controllo guardava solo le interfacce implementate.
     */
    @Test
    void compositeWithAbstractClassComponentShouldPass() {
        assertDoesNotThrow(() ->
                PatternAssertions.assertThat(ShapeGroup.class)
                        .implementsComposite()
                        .forComponent(AbstractShape.class));
    }

    /**
     * Fix 2 — lo scanner verifica tutte le annotazioni: una classe con @GoFSingleton e
     * @GoFObserver entrambe violate deve produrre violazioni di ENTRAMBI i ruoli. Prima del fix
     * solo il primo ruolo nell'ordine di dispatch (Singleton) veniva verificato.
     */
    @Test
    void scannerShouldReportViolationsFromAllAnnotationsOnSameClass() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAnnotationScanner.verify(DualRoleClass.class));
        String report = error.getMessage();

        // Evidenza Singleton: il messaggio sul costruttore non privato.
        assertTrue(report.contains("private"),
                "Il report dovrebbe contenere la violazione Singleton (costruttore non private). Report:\n" + report);
        // Evidenza Observer: solo presente se anche la seconda annotazione è stata verificata.
        assertTrue(report.contains("ChangeListener"),
                "Il report dovrebbe contenere anche la violazione Observer (ruolo ChangeListener), " +
                "prova che lo scanner non si è fermato alla prima annotazione. Report:\n" + report);
    }
}
