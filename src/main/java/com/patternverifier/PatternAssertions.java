package com.patternverifier;

import com.patternverifier.assertions.AbstractFactoryAssert;
import com.patternverifier.assertions.BridgeAssert;
import com.patternverifier.assertions.BuilderAssert;
import com.patternverifier.assertions.StateAssert;
import com.patternverifier.assertions.VisitorAssert;
import com.patternverifier.assertions.CommandAssert;
import com.patternverifier.assertions.ObserverAssert;
import com.patternverifier.assertions.StrategyAssert;
import com.patternverifier.assertions.TemplateMethodAssert;
import com.patternverifier.assertions.AdapterAssert;
import com.patternverifier.assertions.CompositeAssert;
import com.patternverifier.assertions.DecoratorAssert;
import com.patternverifier.assertions.FactoryMethodAssert;
import com.patternverifier.assertions.MediatorAssert;
import com.patternverifier.assertions.PrototypeAssert;
import com.patternverifier.assertions.ProxyAssert;
import com.patternverifier.core.ClassAnalyzer;
import com.patternverifier.core.ClassMetadata;
import com.patternverifier.verifiers.SingletonVerifier;

import java.util.List;

/**
 * Punto di ingresso del DSL.
 *
 * Utilizzo:
 *   PatternAssertions.assertThat(MyClass.class).implementsSingleton();
 */
public class PatternAssertions {

    private final Class<?> clazz;
    private final ClassMetadata metadata;

    private PatternAssertions(Class<?> clazz) {
        this.clazz = clazz;
        this.metadata = ClassAnalyzer.analyze(clazz);
    }

    public static PatternAssertions assertThat(Class<?> clazz) {
        return new PatternAssertions(clazz);
    }

    /**
     * Verifica che la classe implementi correttamente il pattern Singleton.
     * In caso di violazioni lancia AssertionError con il dettaglio di tutti i problemi trovati.
     */
    public PatternAssertions implementsSingleton() {
        List<String> violations = new SingletonVerifier(metadata).verify();
        if (!violations.isEmpty()) {
            throw new AssertionError(formatViolations("Singleton", violations));
        }
        return this;
    }

    public AdapterAssert implementsAdapter() {
        return new AdapterAssert(clazz, metadata);
    }

    public BridgeAssert implementsBridge() {
        return new BridgeAssert(clazz, metadata);
    }

    public StateAssert implementsState() {
        return new StateAssert(clazz, metadata);
    }

    public PatternAssertions implementsChainOfResponsibility() {
        List<String> violations =
                new com.patternverifier.verifiers.ChainOfResponsibilityVerifier(metadata).verify();
        if (!violations.isEmpty()) {
            throw new AssertionError(formatViolations("Chain of Responsibility", violations));
        }
        return this;
    }

    public ProxyAssert implementsProxy() {
        return new ProxyAssert(clazz, metadata);
    }

    public DecoratorAssert implementsDecorator() {
        return new DecoratorAssert(clazz, metadata);
    }

    public CompositeAssert implementsComposite() {
        return new CompositeAssert(clazz, metadata);
    }

    public FactoryMethodAssert implementsFactoryMethod() {
        return new FactoryMethodAssert(clazz, metadata);
    }

    public AbstractFactoryAssert implementsAbstractFactory() {
        return new AbstractFactoryAssert(clazz, metadata);
    }

    public StrategyAssert implementsStrategy() {
        return new StrategyAssert(clazz, metadata);
    }

    public ObserverAssert implementsObserver() {
        return new ObserverAssert(clazz, metadata);
    }

    public CommandAssert implementsCommand() {
        return new CommandAssert(clazz, metadata);
    }

    public BuilderAssert implementsBuilder() {
        return new BuilderAssert(clazz, metadata);
    }

    public TemplateMethodAssert implementsTemplateMethod() {
        return new TemplateMethodAssert(clazz, metadata);
    }

    public VisitorAssert implementsVisitor() {
        return new VisitorAssert(clazz, metadata);
    }

    public PrototypeAssert implementsPrototype() {
        return new PrototypeAssert(clazz, metadata);
    }

    public MediatorAssert implementsMediator() {
        return new MediatorAssert(clazz, metadata);
    }

    private String formatViolations(String pattern, List<String> violations) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(clazz.getSimpleName())
          .append(": violazione pattern ").append(pattern).append("\n");
        violations.forEach(v -> sb.append("  - ").append(v).append("\n"));
        return sb.toString();
    }
}
