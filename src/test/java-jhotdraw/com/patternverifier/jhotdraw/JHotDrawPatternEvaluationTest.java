package com.patternverifier.jhotdraw;

import com.patternverifier.PatternAssertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Valutazione Fase 5: ogni classe concreta del ground truth viene testata contro JHotDraw 5.1
 * reale — una per una, non un'istanza aggregata (decisione: testare tutte le istanze).
 *
 * Fonte dei dati: {@code docs/ground_truth_jhotdraw_pmart.md} (istanze P-MARt, tag "pmart") e
 * {@code docs/jhotdraw51_class_role_index.md} (istanze aggiuntive trovate manualmente, tag
 * "manuale"). Risultati misurati e discussi in {@code docs/note_valutazione_jhotdraw.md}.
 *
 * Struttura: dopo una prima esecuzione esplorativa (ogni classe testata con l'aspettativa
 * "deve passare"), le classi risultate non conformi sono state verificate manualmente sul
 * sorgente e poi convertite in test con {@code assertThrows} — il fallimento è documentato e
 * bloccato come regressione, non semplicemente atteso. Questo mantiene la build verde pur
 * registrando esattamente cosa il tool NON riconosce e perché.
 *
 *   TPR (per classe) = classi che passano assertThat / totale classi testate
 *   Le classi in una sezione "assertThrows" contano come NON passanti nel TPR anche se il
 *   loro test JUnit ha esito positivo (il test verifica che il fallimento avvenga, non che il
 *   pattern sia implementato correttamente).
 *
 * Esecuzione: {@code mvnw test -P jhotdraw-evaluation -Dtest=JHotDrawPatternEvaluationTest}
 */
class JHotDrawPatternEvaluationTest {

    /**
     * Molte classi concrete di JHotDraw (es. le sottoclassi di Handle per punto cardinale)
     * sono package-private — nessun modificatore public. Il tool le legge comunque via ASM
     * (il bytecode non ha restrizioni di accesso Java), ma il nostro codice di test, in un
     * package diverso, non può scrivere il letterale {@code EastHandle.class} a compile-time.
     * Class.forName() aggira il problema: è una risoluzione a runtime, non soggetta alla
     * visibilità Java a compile-time.
     */
    private static Class<?> forName(String fullyQualifiedName) {
        try {
            return Class.forName(fullyQualifiedName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // ==================================================================================
    // Singleton — istanze #85, #86
    // ==================================================================================

    @Tag("pmart")
    @ParameterizedTest
    @MethodSource("singletonPassing")
    void singletonShouldPass(Class<?> singleton) {
        PatternAssertions.assertThat(singleton).implementsSingleton();
    }

    static Stream<Class<?>> singletonPassing() {
        return Stream.of(CH.ifa.draw.util.Clipboard.class);
    }

    /** FN9: Iconkit ha costruttore pubblico, non private. */
    @Tag("pmart")
    @org.junit.jupiter.api.Test
    void singletonIconkitFailsPrivateConstructor() {
        assertThrows(AssertionError.class,
                () -> PatternAssertions.assertThat(CH.ifa.draw.util.Iconkit.class).implementsSingleton());
    }

    // ==================================================================================
    // Composite — istanza #75 (5 classi "composite"), Component = Figure — tutte passano
    // ==================================================================================

    @Tag("pmart")
    @ParameterizedTest
    @MethodSource("compositeClasses")
    void compositeShouldPass(Class<?> composite) {
        PatternAssertions.assertThat(composite)
                .implementsComposite()
                .forComponent(CH.ifa.draw.framework.Figure.class);
    }

    static Stream<Class<?>> compositeClasses() {
        return Stream.of(
                CH.ifa.draw.standard.CompositeFigure.class,
                CH.ifa.draw.figures.GroupFigure.class,
                CH.ifa.draw.samples.pert.PertFigure.class,
                CH.ifa.draw.standard.StandardDrawing.class,
                CH.ifa.draw.samples.javadraw.BouncingDrawing.class
        );
    }

    // ==================================================================================
    // Decorator — istanza #76 (2 concreteDecorator), Component = Figure — entrambe passano
    // ==================================================================================

    @Tag("pmart")
    @ParameterizedTest
    @MethodSource("decoratorClasses")
    void decoratorShouldPass(Class<?> decorator) {
        PatternAssertions.assertThat(decorator)
                .implementsDecorator()
                .forComponent(CH.ifa.draw.framework.Figure.class);
    }

    static Stream<Class<?>> decoratorClasses() {
        return Stream.of(
                CH.ifa.draw.samples.javadraw.AnimationDecorator.class,
                CH.ifa.draw.figures.BorderDecorator.class
        );
    }

    // ==================================================================================
    // Adapter — istanza #73 (21 classi "adapter"), Adaptee = Figure, Target = Handle.
    // 13 passano; 8 falliscono su "non delega mai all'Adaptee" — pur ereditando da
    // AbstractHandle la stessa struttura (campo Figure, interfaccia Handle), la delega
    // comportamentale dipende dal metodo specifico che ciascuna sottoclasse sovrascrive:
    // non tutte le Handle interrogano la Figure per calcolare la propria posizione.
    // ==================================================================================

    @Tag("pmart")
    @ParameterizedTest
    @MethodSource("adapterPassing")
    void adapterShouldPass(Class<?> adapter) {
        PatternAssertions.assertThat(adapter)
                .implementsAdapter()
                .fromAdaptee(CH.ifa.draw.framework.Figure.class)
                .toTarget(CH.ifa.draw.framework.Handle.class);
    }

    static Stream<Class<?>> adapterPassing() {
        return Stream.of(
                CH.ifa.draw.standard.ChangeConnectionEndHandle.class,
                CH.ifa.draw.standard.ChangeConnectionStartHandle.class,
                CH.ifa.draw.figures.ElbowHandle.class,
                CH.ifa.draw.standard.ConnectionHandle.class,
                forName("CH.ifa.draw.standard.EastHandle"),
                forName("CH.ifa.draw.standard.NorthEastHandle"),
                forName("CH.ifa.draw.standard.NorthHandle"),
                forName("CH.ifa.draw.standard.NorthWestHandle"),
                forName("CH.ifa.draw.standard.SouthEastHandle"),
                forName("CH.ifa.draw.standard.SouthHandle"),
                forName("CH.ifa.draw.standard.SouthWestHandle"),
                forName("CH.ifa.draw.standard.WestHandle"),
                forName("CH.ifa.draw.contrib.TriangleRotationHandle")
        );
    }

    @Tag("pmart")
    @ParameterizedTest
    @MethodSource("adapterFailing")
    void adapterShouldFailNoDelegation(Class<?> adapter) {
        assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(adapter)
                        .implementsAdapter()
                        .fromAdaptee(CH.ifa.draw.framework.Figure.class)
                        .toTarget(CH.ifa.draw.framework.Handle.class));
    }

    static Stream<Class<?>> adapterFailing() {
        return Stream.of(
                CH.ifa.draw.standard.LocatorHandle.class,
                CH.ifa.draw.figures.FontSizeHandle.class,
                CH.ifa.draw.standard.NullHandle.class,
                forName("CH.ifa.draw.figures.GroupHandle"),
                CH.ifa.draw.figures.PolyLineHandle.class,
                CH.ifa.draw.contrib.PolygonHandle.class,
                forName("CH.ifa.draw.contrib.PolygonScaleHandle"),
                forName("CH.ifa.draw.figures.RadiusHandle")
        );
    }

    // ==================================================================================
    // Observer — istanza #82 Drawing/DrawingChangeListener: FN1/FN2, entrambe falliscono
    // (naming convention: drawingInvalidated non è update*/on*/handle*, figureInvalidated
    // non è notify*/fire*/dispatch*)
    // ==================================================================================

    @Tag("pmart")
    @ParameterizedTest
    @MethodSource("observerDrawingClasses")
    void observerDrawingShouldFailNamingConvention(Class<?> subject) {
        assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(subject)
                        .implementsObserver()
                        .withObserverInterface(CH.ifa.draw.framework.DrawingChangeListener.class));
    }

    static Stream<Class<?>> observerDrawingClasses() {
        return Stream.of(
                CH.ifa.draw.standard.StandardDrawing.class,
                CH.ifa.draw.samples.javadraw.BouncingDrawing.class
        );
    }

    // Observer — istanza #81 Figure/FigureChangeListener (21 concreteSubject, deduplicate):
    // FN3/FN4, tutte falliscono per la stessa causa (naming convention non canonica)

    @Tag("pmart")
    @ParameterizedTest
    @MethodSource("observerFigureClasses")
    void observerFigureShouldFailNamingConvention(Class<?> subject) {
        assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(subject)
                        .implementsObserver()
                        .withObserverInterface(CH.ifa.draw.framework.FigureChangeListener.class));
    }

    static Stream<Class<?>> observerFigureClasses() {
        return Stream.of(
                CH.ifa.draw.figures.EllipseFigure.class,
                CH.ifa.draw.figures.ImageFigure.class,
                CH.ifa.draw.contrib.PolygonFigure.class,
                CH.ifa.draw.figures.RectangleFigure.class,
                CH.ifa.draw.contrib.DiamondFigure.class,
                CH.ifa.draw.contrib.TriangleFigure.class,
                CH.ifa.draw.figures.RoundRectangleFigure.class,
                CH.ifa.draw.figures.TextFigure.class,
                CH.ifa.draw.samples.net.NodeFigure.class,
                CH.ifa.draw.figures.NumberTextFigure.class,
                CH.ifa.draw.figures.GroupFigure.class,
                CH.ifa.draw.samples.pert.PertFigure.class,
                CH.ifa.draw.standard.StandardDrawing.class,
                CH.ifa.draw.samples.javadraw.BouncingDrawing.class,
                CH.ifa.draw.samples.javadraw.AnimationDecorator.class,
                CH.ifa.draw.figures.BorderDecorator.class,
                CH.ifa.draw.figures.PolyLineFigure.class,
                CH.ifa.draw.figures.LineConnection.class,
                CH.ifa.draw.figures.ElbowConnection.class,
                CH.ifa.draw.samples.pert.PertDependency.class,
                CH.ifa.draw.figures.LineFigure.class
        );
    }

    // Observer — istanza #82b Connector/ConnectionFigure: SCOPERTA NUOVA, non prevista.
    // Il javadoc di ConnectionFigure descrive un rapporto Observer, ma strutturalmente
    // ConnectionFigure INTERROGA il Connector (findStart/findEnd) invece di essere
    // notificato da esso — un modello "pull" più vicino a Strategy che a Observer "push".

    @Tag("pmart")
    @ParameterizedTest
    @MethodSource("observerConnectorClasses")
    void observerConnectorShouldFailPullNotPushModel(Class<?> subject) {
        assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(subject)
                        .implementsObserver()
                        .withObserverInterface(CH.ifa.draw.framework.ConnectionFigure.class));
    }

    static Stream<Class<?>> observerConnectorClasses() {
        return Stream.of(
                CH.ifa.draw.standard.ChopBoxConnector.class,
                CH.ifa.draw.standard.LocatorConnector.class,
                CH.ifa.draw.figures.ShortestDistanceConnector.class
        );
    }

    // ==================================================================================
    // Strategy — istanza #90 Painter, #92 PointConstrainer: passano
    // ==================================================================================

    @Tag("pmart")
    @ParameterizedTest
    @MethodSource("strategyPainterClasses")
    void strategyPainterShouldPass(Class<?> context) {
        PatternAssertions.assertThat(context)
                .implementsStrategy()
                .withStrategyInterface(CH.ifa.draw.framework.Painter.class);
    }

    static Stream<Class<?>> strategyPainterClasses() {
        return Stream.of(CH.ifa.draw.standard.StandardDrawingView.class);
    }

    @Tag("pmart")
    @ParameterizedTest
    @MethodSource("strategyConstrainerClasses")
    void strategyConstrainerShouldPass(Class<?> context) {
        PatternAssertions.assertThat(context)
                .implementsStrategy()
                .withStrategyInterface(CH.ifa.draw.framework.PointConstrainer.class);
    }

    static Stream<Class<?>> strategyConstrainerClasses() {
        return Stream.of(CH.ifa.draw.standard.StandardDrawingView.class);
    }

    // Strategy — istanza #91 Locator: 3/4 passano. PolygonHandle, LocatorConnector,
    // LocatorHandle hanno un campo Locator; TextFigure no (probabile sovra-inclusione P-MARt).
    // Nota: FN7 originariamente previsto (LocatorHandle fallisce per mancanza di setter) era
    // sbagliato — StrategyVerifier accetta setter OPPURE costruttore, LocatorHandle passa.

    @Tag("pmart")
    @ParameterizedTest
    @MethodSource("strategyLocatorPassing")
    void strategyLocatorShouldPass(Class<?> context) {
        PatternAssertions.assertThat(context)
                .implementsStrategy()
                .withStrategyInterface(CH.ifa.draw.framework.Locator.class);
    }

    static Stream<Class<?>> strategyLocatorPassing() {
        return Stream.of(
                CH.ifa.draw.contrib.PolygonHandle.class,
                CH.ifa.draw.standard.LocatorConnector.class,
                CH.ifa.draw.standard.LocatorHandle.class
        );
    }

    /** Sovra-inclusione P-MARt: TextFigure non ha un campo di tipo Locator. */
    @Tag("pmart")
    @org.junit.jupiter.api.Test
    void strategyLocatorTextFigureShouldFailNoField() {
        assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(CH.ifa.draw.figures.TextFigure.class)
                        .implementsStrategy()
                        .withStrategyInterface(CH.ifa.draw.framework.Locator.class));
    }

    // Strategy — istanza #89 Connector: SCOPERTA NUOVA. Solo LineConnection e
    // ChangeConnectionHandle (2/12) hanno davvero un campo Connector dedicato. Le altre 10
    // classi elencate da P-MARt come "context" probabilmente USANO un Connector in qualche
    // metodo (es. via connectorAt()) senza MANTENERLO come proprio stato — una distinzione
    // che il nostro verifier fa correttamente ma il ruolo P-MARt non cattura.

    @Tag("pmart")
    @ParameterizedTest
    @MethodSource("strategyConnectorPassing")
    void strategyConnectorShouldPass(Class<?> context) {
        PatternAssertions.assertThat(context)
                .implementsStrategy()
                .withStrategyInterface(CH.ifa.draw.framework.Connector.class);
    }

    static Stream<Class<?>> strategyConnectorPassing() {
        return Stream.of(
                CH.ifa.draw.figures.LineConnection.class,
                CH.ifa.draw.standard.ChangeConnectionHandle.class
        );
    }

    @Tag("pmart")
    @ParameterizedTest
    @MethodSource("strategyConnectorFailing")
    void strategyConnectorShouldFailNoOwnField(Class<?> context) {
        assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(context)
                        .implementsStrategy()
                        .withStrategyInterface(CH.ifa.draw.framework.Connector.class));
    }

    static Stream<Class<?>> strategyConnectorFailing() {
        return Stream.of(
                CH.ifa.draw.contrib.PolygonFigure.class,
                CH.ifa.draw.figures.EllipseFigure.class,
                CH.ifa.draw.figures.PolyLineFigure.class,
                CH.ifa.draw.figures.RoundRectangleFigure.class,
                CH.ifa.draw.samples.net.NodeFigure.class,
                CH.ifa.draw.standard.AbstractFigure.class,
                CH.ifa.draw.standard.DecoratorFigure.class,
                CH.ifa.draw.framework.ConnectionFigure.class,
                CH.ifa.draw.standard.ConnectionTool.class,
                CH.ifa.draw.standard.ConnectionHandle.class
        );
    }

    // Strategy — LineDecoration: SOLO ANALISI MANUALE, assente da P-MARt (vedi
    // ground_truth_jhotdraw_pmart.md, sezione "Strategy — LineDecoration") — passa

    @Tag("manuale")
    @ParameterizedTest
    @MethodSource("strategyLineDecorationClasses")
    void strategyLineDecorationShouldPass(Class<?> context) {
        PatternAssertions.assertThat(context)
                .implementsStrategy()
                .withStrategyInterface(CH.ifa.draw.figures.LineDecoration.class);
    }

    static Stream<Class<?>> strategyLineDecorationClasses() {
        return Stream.of(CH.ifa.draw.figures.PolyLineFigure.class);
    }

    // ==================================================================================
    // State — istanza #87 StandardDrawingView, istanza #88 SelectionTool: SCOPERTE NUOVE,
    // entrambe falliscono. Vedi note_valutazione_jhotdraw.md per la spiegazione completa:
    // StandardDrawingView.tool() delega a fEditor.tool() (il vero Context è DrawingEditor,
    // il Mediator — attribuzione sbagliata sia nostra che di P-MARt); SelectionTool ha il
    // campo fChild ma nessun metodo di transizione richiamabile dall'esterno.
    // ==================================================================================

    @Tag("pmart")
    @org.junit.jupiter.api.Test
    void stateStandardDrawingViewShouldFailDelegatesToEditor() {
        assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(CH.ifa.draw.standard.StandardDrawingView.class)
                        .implementsState()
                        .withStateInterface(CH.ifa.draw.framework.Tool.class));
    }

    @Tag("pmart")
    @org.junit.jupiter.api.Test
    void stateSelectionToolShouldFailNoExternalTransition() {
        assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(CH.ifa.draw.standard.SelectionTool.class)
                        .implementsState()
                        .withStateInterface(CH.ifa.draw.framework.Tool.class));
    }

    // ==================================================================================
    // Command — istanza #74 (13 concreteCommand: 8 diretti + 5 via FigureTransferCommand),
    // Receiver = DrawingView — tutte passano, incluse le 5 a 2 livelli di ereditarietà
    // (dimostra la correzione isDescendantOf del 2026-07-04)
    // ==================================================================================

    @Tag("pmart")
    @ParameterizedTest
    @MethodSource("commandClasses")
    void commandShouldPass(Class<?> concreteCommand) {
        PatternAssertions.assertThat(concreteCommand)
                .implementsCommand()
                .withCommandInterface(CH.ifa.draw.util.Command.class)
                .withReceiver(CH.ifa.draw.framework.DrawingView.class);
    }

    static Stream<Class<?>> commandClasses() {
        return Stream.of(
                CH.ifa.draw.standard.AlignCommand.class,
                CH.ifa.draw.standard.BringToFrontCommand.class,
                CH.ifa.draw.standard.ChangeAttributeCommand.class,
                CH.ifa.draw.standard.SendToBackCommand.class,
                CH.ifa.draw.standard.ToggleGridCommand.class,
                CH.ifa.draw.figures.GroupCommand.class,
                CH.ifa.draw.figures.UngroupCommand.class,
                CH.ifa.draw.figures.InsertImageCommand.class,
                CH.ifa.draw.standard.CopyCommand.class,
                CH.ifa.draw.standard.CutCommand.class,
                CH.ifa.draw.standard.DeleteCommand.class,
                CH.ifa.draw.standard.DuplicateCommand.class,
                CH.ifa.draw.standard.PasteCommand.class
        );
    }

    // ==================================================================================
    // Template Method — istanza #93 AbstractFigure/moveBy: passa. Il verifier prende in
    // ingresso l'AbstractClass, non le concreteClass elencate da P-MARt.
    //
    // Istanza #94 (AttributeFigure) esclusa: non ridichiara moveBy() (limite noto, non
    // esteso a TemplateMethodBodyAnalyzer — vedi architettura.md). Il suo vero template
    // method è probabilmente draw() (chiama drawBackground()/drawFrame()), ma questi sono
    // hook concreti non abstract — domanda aperta, non testata qui.
    // ==================================================================================

    @Tag("pmart")
    @org.junit.jupiter.api.Test
    void templateMethodAbstractFigureShouldPass() {
        PatternAssertions.assertThat(CH.ifa.draw.standard.AbstractFigure.class)
                .implementsTemplateMethod()
                .withTemplateMethod("moveBy");
    }

    // ==================================================================================
    // Factory Method — istanza #77 Figure.connectorAt / Connector (5 concreteCreator) — passa
    // Istanza #78 (Figure.handles / Handle) esclusa: handles() ritorna Vector, non Handle —
    // non rispetta il modello "un solo Product" del verifier (batch factory method, variante
    // non canonica).
    // Istanza #79 (SelectionTool) esclusa: creator e concreteCreator coincidono nella stessa
    // classe concreta, non c'è una classe Creator astratta distinta — non si adatta al
    // modello Creator/ConcreteCreator verificato dal tool.
    // ==================================================================================

    @Tag("pmart")
    @ParameterizedTest
    @MethodSource("factoryMethodConnectorClasses")
    void factoryMethodConnectorShouldPass(Class<?> concreteCreator) {
        PatternAssertions.assertThat(CH.ifa.draw.framework.Figure.class)
                .implementsFactoryMethod()
                .withAbstractFactoryMethod("connectorAt", CH.ifa.draw.framework.Connector.class)
                .withConcreteCreator(concreteCreator);
    }

    static Stream<Class<?>> factoryMethodConnectorClasses() {
        return Stream.of(
                CH.ifa.draw.standard.AbstractFigure.class,
                CH.ifa.draw.figures.EllipseFigure.class,
                CH.ifa.draw.contrib.PolygonFigure.class,
                CH.ifa.draw.figures.RoundRectangleFigure.class,
                CH.ifa.draw.figures.PolyLineFigure.class
        );
    }
}
