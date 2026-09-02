package com.patternverifier.annotationscanner;

import com.patternverifier.PatternAnnotationScanner;
import com.patternverifier.abstractfactory.correct.DarkThemeFactory;
import com.patternverifier.adapter.correct.SocketAdapter;
import com.patternverifier.bridge.correct.RemoteControl;
import com.patternverifier.builder.correct.PersonBuilder;
import com.patternverifier.chainofresponsibility.correct.RequestHandler;
import com.patternverifier.command.correct.PrintCommand;
import com.patternverifier.command.wrong.NoReceiverCommand;
import com.patternverifier.composite.correct.FileSystemDirectory;
import com.patternverifier.decorator.correct.BoldDecorator;
import com.patternverifier.factorymethod.correct.DogFactory;
import com.patternverifier.observer.correct.EventBus;
import com.patternverifier.observer.wrong.MissingCollectionSubject;
import com.patternverifier.proxy.correct.ImageProxy;
import com.patternverifier.singleton.correct.DatabaseConnection;
import com.patternverifier.singleton.wrong.PublicConstructorSingleton;
import com.patternverifier.state.correct.TrafficLight;
import com.patternverifier.strategy.correct.SorterWithSetter;
import com.patternverifier.templatemethod.correct.DataProcessor;
import com.patternverifier.visitor.correct.HtmlExporter;
import com.patternverifier.prototype.correct.Circle;
import com.patternverifier.mediator.correct.ChatRoom;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PatternAnnotationScannerTest {

    @Test
    void correctSingletonAnnotationShouldPass() {
        PatternAnnotationScanner.verify(DatabaseConnection.class);
    }

    @Test
    void correctObserverAnnotationShouldPass() {
        PatternAnnotationScanner.verify(EventBus.class);
    }

    @Test
    void correctCommandAnnotationShouldPass() {
        PatternAnnotationScanner.verify(PrintCommand.class);
    }

    @Test
    void multipleCorrectAnnotatedClassesShouldPass() {
        PatternAnnotationScanner.verify(
                DatabaseConnection.class,
                EventBus.class,
                PrintCommand.class
        );
    }

    @Test
    void unannotatedClassShouldBeIgnored() {
        // String non ha @GoFXxx — lo scanner la ignora silenziosamente
        PatternAnnotationScanner.verify(String.class);
    }

    @Test
    void brokenSingletonShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAnnotationScanner.verify(PublicConstructorSingleton.class)
        );
        assertTrue(error.getMessage().contains("private"),
                "Dovrebbe segnalare il costruttore non private");
    }

    @Test
    void observerMissingCollectionShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAnnotationScanner.verify(MissingCollectionSubject.class)
        );
        assertTrue(error.getMessage().contains("Collection"),
                "Dovrebbe segnalare il campo Collection mancante");
    }

    @Test
    void commandWithNoReceiverDelegationShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAnnotationScanner.verify(NoReceiverCommand.class)
        );
        assertTrue(error.getMessage().contains("Receiver") || error.getMessage().contains("delega"),
                "Dovrebbe segnalare che il ConcreteCommand non delega al Receiver");
    }

    @Test
    void allEighteenPatternsAnnotatedCorrectlyShouldPass() {
        PatternAnnotationScanner.verify(
                DatabaseConnection.class,     // @GoFSingleton
                DogFactory.class,             // @GoFFactoryMethod
                DarkThemeFactory.class,       // @GoFAbstractFactory
                PersonBuilder.class,          // @GoFBuilder
                DataProcessor.class,          // @GoFTemplateMethod
                PrintCommand.class,           // @GoFCommand
                RequestHandler.class,         // @GoFChainOfResponsibility
                TrafficLight.class,           // @GoFState
                SorterWithSetter.class,       // @GoFStrategy
                BoldDecorator.class,          // @GoFDecorator
                EventBus.class,               // @GoFObserver
                ImageProxy.class,             // @GoFProxy
                SocketAdapter.class,          // @GoFAdapter
                RemoteControl.class,          // @GoFBridge
                FileSystemDirectory.class,    // @GoFComposite
                HtmlExporter.class,           // @GoFVisitor
                Circle.class,                 // @GoFPrototype
                ChatRoom.class                // @GoFMediator
        );
    }

    @Test
    void multipleViolationsShouldAllBeReportedTogether() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAnnotationScanner.verify(
                        PublicConstructorSingleton.class,
                        MissingCollectionSubject.class
                )
        );
        String msg = error.getMessage();
        assertTrue(msg.contains("PublicConstructorSingleton"),
                "Dovrebbe citare la classe con il Singleton rotto");
        assertTrue(msg.contains("MissingCollectionSubject"),
                "Dovrebbe citare la classe con l'Observer rotto");
    }
}
