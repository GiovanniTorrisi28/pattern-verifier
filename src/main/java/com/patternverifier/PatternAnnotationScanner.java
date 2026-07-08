package com.patternverifier;

import com.patternverifier.annotations.*;
import com.patternverifier.core.ClassAnalyzer;
import com.patternverifier.core.ClassMetadata;
import com.patternverifier.verifiers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Seconda API pubblica del tool: scansiona le annotazioni @GoFXxx presenti sulle classi
 * passate, esegue i verifier corrispondenti e lancia AssertionError se trova violazioni.
 *
 * Uso tipico in un test JUnit 5:
 *   PatternAnnotationScanner.verify(PrintCommand.class, EventBus.class, TrafficLight.class);
 *
 * Le classi prive di annotazioni @GoFXxx vengono ignorate silenziosamente. Una classe può
 * dichiarare più annotazioni @GoFXxx contemporaneamente (es. una figura di JHotDraw che gioca
 * sia il ruolo di ConcreteSubject in Observer sia di ConcreteCreator in Factory Method): in tal
 * caso vengono verificati TUTTI i ruoli dichiarati e le violazioni aggregate — non solo il primo.
 */
public class PatternAnnotationScanner {

    public static void verify(Class<?>... classes) {
        StringBuilder report = new StringBuilder();
        for (Class<?> clazz : classes) {
            List<String> violations = scanClass(clazz);
            if (!violations.isEmpty()) {
                report.append("\n[").append(clazz.getSimpleName()).append("]\n");
                violations.forEach(v -> report.append("  - ").append(v).append("\n"));
            }
        }
        if (report.length() > 0) {
            throw new AssertionError("\nVerifica pattern GoF fallita:" + report);
        }
    }

    // Verifica TUTTE le annotazioni @GoFXxx presenti sulla classe (non solo la prima): una
    // classe può giocare più ruoli di pattern contemporaneamente, e le violazioni di ciascun
    // ruolo vanno tutte segnalate. I messaggi dei verifier sono già auto-descrittivi del pattern
    // ("il Singleton deve...", "il Decorator deve...") quindi restano distinguibili nel report.
    private static List<String> scanClass(Class<?> clazz) {
        List<String> violations = new ArrayList<>();
        if (clazz.isAnnotationPresent(GoFSingleton.class)) {
            violations.addAll(new SingletonVerifier(ClassAnalyzer.analyze(clazz)).verify());
        }
        if (clazz.isAnnotationPresent(GoFFactoryMethod.class)) {
            violations.addAll(scanFactoryMethod(clazz, clazz.getAnnotation(GoFFactoryMethod.class)));
        }
        if (clazz.isAnnotationPresent(GoFAbstractFactory.class)) {
            violations.addAll(scanAbstractFactory(clazz, clazz.getAnnotation(GoFAbstractFactory.class)));
        }
        if (clazz.isAnnotationPresent(GoFBuilder.class)) {
            violations.addAll(scanBuilder(clazz, clazz.getAnnotation(GoFBuilder.class)));
        }
        if (clazz.isAnnotationPresent(GoFTemplateMethod.class)) {
            violations.addAll(scanTemplateMethod(clazz, clazz.getAnnotation(GoFTemplateMethod.class)));
        }
        if (clazz.isAnnotationPresent(GoFCommand.class)) {
            violations.addAll(scanCommand(clazz, clazz.getAnnotation(GoFCommand.class)));
        }
        if (clazz.isAnnotationPresent(GoFChainOfResponsibility.class)) {
            violations.addAll(new ChainOfResponsibilityVerifier(ClassAnalyzer.analyze(clazz)).verify());
        }
        if (clazz.isAnnotationPresent(GoFState.class)) {
            violations.addAll(scanState(clazz, clazz.getAnnotation(GoFState.class)));
        }
        if (clazz.isAnnotationPresent(GoFStrategy.class)) {
            violations.addAll(scanStrategy(clazz, clazz.getAnnotation(GoFStrategy.class)));
        }
        if (clazz.isAnnotationPresent(GoFDecorator.class)) {
            violations.addAll(scanDecorator(clazz, clazz.getAnnotation(GoFDecorator.class)));
        }
        if (clazz.isAnnotationPresent(GoFObserver.class)) {
            violations.addAll(scanObserver(clazz, clazz.getAnnotation(GoFObserver.class)));
        }
        if (clazz.isAnnotationPresent(GoFProxy.class)) {
            violations.addAll(scanProxy(clazz, clazz.getAnnotation(GoFProxy.class)));
        }
        if (clazz.isAnnotationPresent(GoFAdapter.class)) {
            violations.addAll(scanAdapter(clazz, clazz.getAnnotation(GoFAdapter.class)));
        }
        if (clazz.isAnnotationPresent(GoFBridge.class)) {
            violations.addAll(scanBridge(clazz, clazz.getAnnotation(GoFBridge.class)));
        }
        if (clazz.isAnnotationPresent(GoFComposite.class)) {
            violations.addAll(scanComposite(clazz, clazz.getAnnotation(GoFComposite.class)));
        }
        if (clazz.isAnnotationPresent(GoFVisitor.class)) {
            violations.addAll(scanVisitor(clazz, clazz.getAnnotation(GoFVisitor.class)));
        }
        return violations;
    }

    private static List<String> scanFactoryMethod(Class<?> clazz, GoFFactoryMethod ann) {
        ClassMetadata creator        = ClassAnalyzer.analyze(ann.creator());
        ClassMetadata concreteCreator = ClassAnalyzer.analyze(clazz);
        ClassMetadata product        = ClassAnalyzer.analyze(ann.product());
        return new FactoryMethodVerifier(creator, concreteCreator, product, ann.factoryMethod()).verify();
    }

    private static List<String> scanAbstractFactory(Class<?> clazz, GoFAbstractFactory ann) {
        ClassMetadata abstractFactory  = ClassAnalyzer.analyze(ann.abstractFactory());
        ClassMetadata concreteFactory  = ClassAnalyzer.analyze(clazz);
        List<Class<?>> products        = Arrays.asList(ann.products());
        return new AbstractFactoryVerifier(abstractFactory, products, concreteFactory).verify();
    }

    private static List<String> scanBuilder(Class<?> clazz, GoFBuilder ann) {
        ClassMetadata builder  = ClassAnalyzer.analyze(clazz);
        ClassMetadata product  = ClassAnalyzer.analyze(ann.product());
        return new BuilderVerifier(builder, product).verify();
    }

    private static List<String> scanTemplateMethod(Class<?> clazz, GoFTemplateMethod ann) {
        ClassMetadata abstractClass = ClassAnalyzer.analyze(clazz);
        return new TemplateMethodVerifier(clazz, abstractClass, ann.templateMethod()).verify();
    }

    private static List<String> scanCommand(Class<?> clazz, GoFCommand ann) {
        ClassMetadata concreteCommand   = ClassAnalyzer.analyze(clazz);
        ClassMetadata commandInterface  = ClassAnalyzer.analyze(ann.commandInterface());
        ClassMetadata receiver          = ann.receiver() == Void.class
                ? null
                : ClassAnalyzer.analyze(ann.receiver());
        return new CommandVerifier(concreteCommand, commandInterface, receiver).verify();
    }

    private static List<String> scanState(Class<?> clazz, GoFState ann) {
        ClassMetadata context = ClassAnalyzer.analyze(clazz);
        ClassMetadata state   = ClassAnalyzer.analyze(ann.state());
        return new StateVerifier(context, state).verify();
    }

    private static List<String> scanStrategy(Class<?> clazz, GoFStrategy ann) {
        ClassMetadata context  = ClassAnalyzer.analyze(clazz);
        ClassMetadata strategy = ClassAnalyzer.analyze(ann.strategy());
        return new StrategyVerifier(context, strategy).verify();
    }

    private static List<String> scanDecorator(Class<?> clazz, GoFDecorator ann) {
        ClassMetadata decorator = ClassAnalyzer.analyze(clazz);
        ClassMetadata component = ClassAnalyzer.analyze(ann.component());
        return new DecoratorVerifier(decorator, component).verify();
    }

    private static List<String> scanObserver(Class<?> clazz, GoFObserver ann) {
        ClassMetadata subject  = ClassAnalyzer.analyze(clazz);
        ClassMetadata observer = ClassAnalyzer.analyze(ann.observer());
        return new ObserverVerifier(subject, observer).verify();
    }

    private static List<String> scanProxy(Class<?> clazz, GoFProxy ann) {
        ClassMetadata proxy       = ClassAnalyzer.analyze(clazz);
        ClassMetadata subject     = ClassAnalyzer.analyze(ann.subject());
        ClassMetadata realSubject = ClassAnalyzer.analyze(ann.realSubject());
        return new ProxyVerifier(proxy, subject, realSubject).verify();
    }

    private static List<String> scanAdapter(Class<?> clazz, GoFAdapter ann) {
        ClassMetadata adapter = ClassAnalyzer.analyze(clazz);
        ClassMetadata adaptee = ClassAnalyzer.analyze(ann.adaptee());
        ClassMetadata target  = ClassAnalyzer.analyze(ann.target());
        return new AdapterVerifier(adapter, adaptee, target).verify();
    }

    private static List<String> scanBridge(Class<?> clazz, GoFBridge ann) {
        ClassMetadata abstraction = ClassAnalyzer.analyze(clazz);
        ClassMetadata implementor = ClassAnalyzer.analyze(ann.implementor());
        return new BridgeVerifier(abstraction, implementor).verify();
    }

    private static List<String> scanComposite(Class<?> clazz, GoFComposite ann) {
        ClassMetadata composite = ClassAnalyzer.analyze(clazz);
        ClassMetadata component = ClassAnalyzer.analyze(ann.component());
        return new CompositeVerifier(composite, component).verify();
    }

    private static List<String> scanVisitor(Class<?> clazz, GoFVisitor ann) {
        ClassMetadata concreteVisitor   = ClassAnalyzer.analyze(clazz);
        ClassMetadata visitorInterface  = ClassAnalyzer.analyze(ann.visitorInterface());
        ClassMetadata element           = ClassAnalyzer.analyze(ann.element());
        return new VisitorVerifier(concreteVisitor, visitorInterface, element).verify();
    }
}
