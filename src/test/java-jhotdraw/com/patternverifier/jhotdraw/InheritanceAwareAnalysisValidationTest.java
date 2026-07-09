package com.patternverifier.jhotdraw;

import CH.ifa.draw.figures.GroupFigure;
import CH.ifa.draw.framework.DrawingView;
import CH.ifa.draw.framework.Figure;
import CH.ifa.draw.standard.CopyCommand;
import CH.ifa.draw.util.Command;
import com.patternverifier.PatternAssertions;
import org.junit.jupiter.api.Test;

/**
 * Verifica puntuale che ClassAnalyzer.analyze() sia ora inheritance-aware.
 *
 * GroupFigure (com/CH.ifa.draw.figures) estende CompositeFigure senza ridichiarare
 * il campo fFigures né i metodi add()/remove(): li eredita da CompositeFigure, che a
 * sua volta implementa Figure solo indirettamente (via AbstractFigure). Prima della
 * correzione (2026-07-04), analizzare GroupFigure da solo non vedeva nessuno di questi
 * tre elementi — un falso negativo causato dal limite del tool, non da una reale
 * violazione strutturale del pattern. Questo test prova che dopo la correzione il
 * verifier segue la gerarchia e riconosce correttamente GroupFigure come Composite.
 *
 * Non è parte della valutazione completa di Fase 5 (JHotDrawPatternEvaluationTest,
 * ancora da scrivere) — è solo la prova puntuale che l'estensione architetturale
 * funziona end-to-end su codice reale, non solo sulle fixture sintetiche.
 */
class InheritanceAwareAnalysisValidationTest {

    @Test
    void groupFigureShouldPassCompositeThroughInheritedStructure() {
        // fFigures, add(Figure) e remove(Figure) sono dichiarati in CompositeFigure,
        // non in GroupFigure. L'interfaccia Figure è implementata da AbstractFigure,
        // due livelli sopra GroupFigure nella gerarchia.
        PatternAssertions.assertThat(GroupFigure.class)
                .implementsComposite()
                .forComponent(Figure.class);
    }

    /**
     * CopyCommand estende Command a due livelli (CopyCommand → FigureTransferCommand →
     * Command), non direttamente. Prima della correzione (2026-07-04), i verifier che
     * confrontavano solo il nome della superclasse diretta (Command, AbstractFactory,
     * Bridge, FactoryMethod, Visitor) segnalavano "non estende Command" — un falso
     * negativo, non una violazione reale. `TypeHierarchy.isAssignable` (dal 2026-07-08
     * usato anche per questo check, sostituisce `ClassMetadata.isDescendantOf` rimosso
     * perché diventato ridondante) risale l'intera catena delle superclassi via bytecode.
     */
    @Test
    void copyCommandShouldPassCommandThroughTwoLevelInheritance() {
        PatternAssertions.assertThat(CopyCommand.class)
                .implementsCommand()
                .withCommandInterface(Command.class)
                .withReceiver(DrawingView.class);
    }
}
