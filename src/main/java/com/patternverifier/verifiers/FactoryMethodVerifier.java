package com.patternverifier.verifiers;

import com.patternverifier.core.ClassMetadata;

import java.util.ArrayList;
import java.util.List;

public class FactoryMethodVerifier {

    private final ClassMetadata creator;
    private final ClassMetadata concreteCreator;
    private final ClassMetadata product;
    private final String factoryMethodName;

    public FactoryMethodVerifier(ClassMetadata creator, ClassMetadata concreteCreator,
                                  ClassMetadata product, String factoryMethodName) {
        this.creator = creator;
        this.concreteCreator = concreteCreator;
        this.product = product;
        this.factoryMethodName = factoryMethodName;
    }

    public List<String> verify() {
        List<String> violations = new ArrayList<>();
        checkCreatorIsAbstract(violations);
        checkCreatorHasAbstractFactoryMethod(violations);
        checkConcreteCreatorExtendsCreator(violations);
        checkConcreteCreatorOverridesFactoryMethod(violations);
        return violations;
    }

    private void checkCreatorIsAbstract(List<String> violations) {
        if (!creator.isAbstract() && !creator.isInterface()) {
            violations.add(
                creator.getSimpleName() + " non è una classe astratta né un'interfaccia" +
                " — il Creator deve essere astratto per delegare la creazione alle sottoclassi"
            );
        }
    }

    private void checkCreatorHasAbstractFactoryMethod(List<String> violations) {
        boolean hasMethod = creator.getMethods().stream()
                .anyMatch(m -> m.getName().equals(factoryMethodName)
                            && m.isAbstract()
                            && m.getReturnTypeName().equals(product.getClassName()));
        if (!hasMethod) {
            violations.add(
                creator.getSimpleName() + " non ha un metodo astratto '" + factoryMethodName +
                "' che restituisce " + product.getSimpleName() +
                " — il Creator deve dichiarare il factory method come astratto"
            );
        }
    }

    private void checkConcreteCreatorExtendsCreator(List<String> violations) {
        boolean connected = creator.getClassName().equals(concreteCreator.getSuperClassName())
                || concreteCreator.getInterfaces().contains(creator.getClassName())
                || concreteCreator.isDescendantOf(creator.getClassName());
        if (!connected) {
            violations.add(
                concreteCreator.getSimpleName() + " non estende né implementa " + creator.getSimpleName() +
                " — il ConcreteCreator deve estendere il Creator"
            );
        }
    }

    private void checkConcreteCreatorOverridesFactoryMethod(List<String> violations) {
        boolean overrides = concreteCreator.getMethods().stream()
                .anyMatch(m -> m.getName().equals(factoryMethodName) && !m.isAbstract());
        if (!overrides) {
            violations.add(
                concreteCreator.getSimpleName() + " non implementa il metodo '" + factoryMethodName +
                "' — il ConcreteCreator deve fare override del factory method"
            );
        }
    }
}
