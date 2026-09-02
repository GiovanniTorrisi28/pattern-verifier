package com.patternverifier.verifiers;

import com.patternverifier.core.ClassMetadata;
import com.patternverifier.core.TypeHierarchy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AbstractFactoryVerifier {

    private final ClassMetadata abstractFactory;
    private final List<String> productTypeNames;
    private final ClassMetadata concreteFactory;

    public AbstractFactoryVerifier(ClassMetadata abstractFactory, List<Class<?>> products,
                                   ClassMetadata concreteFactory) {
        this.abstractFactory = abstractFactory;
        this.productTypeNames = products.stream()
                .map(Class::getName)
                .collect(Collectors.toList());
        this.concreteFactory = concreteFactory;
    }

    public List<String> verify() {
        List<String> violations = new ArrayList<>();
        checkDeclaresProductFamily(violations);
        checkAbstractFactoryIsAbstract(violations);
        checkAbstractFactoryHasAllFactoryMethods(violations);
        checkConcreteFactoryImplementsAbstractFactory(violations);
        checkConcreteFactoryOverridesAllMethods(violations);
        return violations;
    }

    // L'intento GoF dell'Abstract Factory è creare "famiglie di oggetti correlati": con un solo
    // prodotto il pattern è strutturalmente indistinguibile da un Factory Method, e la verifica
    // perderebbe il proprio contenuto informativo. Il minimo di due prodotti è quindi una
    // proprietà necessaria del pattern, non una convenzione d'uso.
    private void checkDeclaresProductFamily(List<String> violations) {
        if (productTypeNames.size() < 2) {
            violations.add(abstractFactory.getSimpleName()
                    + " dichiara " + productTypeNames.size() + " prodotto"
                    + " — l'AbstractFactory deve creare una famiglia di almeno 2 prodotti"
                    + " correlati, altrimenti è strutturalmente un Factory Method");
        }
    }

    private void checkAbstractFactoryIsAbstract(List<String> violations) {
        if (!abstractFactory.isInterface() && !abstractFactory.isAbstract()) {
            violations.add(abstractFactory.getSimpleName()
                    + " non è né un'interfaccia né una classe astratta"
                    + " — l'AbstractFactory deve essere astratta");
        }
    }

    private void checkAbstractFactoryHasAllFactoryMethods(List<String> violations) {
        for (String productType : productTypeNames) {
            boolean found = abstractFactory.getMethods().stream()
                    .filter(m -> !m.isConstructor())
                    .anyMatch(m -> m.getReturnTypeName().equals(productType));
            if (!found) {
                violations.add(abstractFactory.getSimpleName()
                        + " non ha un factory method che restituisce "
                        + simpleNameOf(productType));
            }
        }
    }

    private void checkConcreteFactoryImplementsAbstractFactory(List<String> violations) {
        if (!TypeHierarchy.isAssignable(concreteFactory.getClassName(), abstractFactory.getClassName())) {
            violations.add(concreteFactory.getSimpleName()
                    + " non implementa né estende "
                    + abstractFactory.getSimpleName()
                    + " — il ConcreteFactory deve implementare l'AbstractFactory");
        }
    }

    private void checkConcreteFactoryOverridesAllMethods(List<String> violations) {
        for (String productType : productTypeNames) {
            abstractFactory.getMethods().stream()
                    .filter(m -> !m.isConstructor() && m.getReturnTypeName().equals(productType))
                    .forEach(abstractMethod -> {
                        boolean overridden = concreteFactory.getMethods().stream()
                                .anyMatch(m -> m.getName().equals(abstractMethod.getName())
                                        && !m.isAbstract());
                        if (!overridden) {
                            violations.add(concreteFactory.getSimpleName()
                                    + " non implementa il factory method '"
                                    + abstractMethod.getName()
                                    + "' per il prodotto "
                                    + simpleNameOf(productType));
                        }
                    });
        }
    }

    private String simpleNameOf(String fullyQualified) {
        int dot = fullyQualified.lastIndexOf('.');
        return dot >= 0 ? fullyQualified.substring(dot + 1) : fullyQualified;
    }
}
