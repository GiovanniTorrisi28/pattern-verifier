package com.patternverifier.verifiers;

import com.patternverifier.core.ClassMetadata;
import com.patternverifier.core.CollectionTypes;
import com.patternverifier.core.MethodInfo;
import com.patternverifier.core.TypeHierarchy;

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
                            && returnsProduct(m));
        if (!hasMethod) {
            violations.add(
                creator.getSimpleName() + " non ha un metodo astratto '" + factoryMethodName +
                "' che restituisce " + product.getSimpleName() +
                " (o una collezione di " + product.getSimpleName() + ") — il Creator deve" +
                " dichiarare il factory method come astratto"
            );
        }
    }

    /**
     * Vero se il metodo restituisce direttamente il Product (variante canonica GoF), oppure una
     * collezione il cui argomento generico è il Product — la variante "a lotti" in cui le
     * sottoclassi decidono quali e quanti Product istanziare in un colpo solo (es. {@code
     * Vector<Handle> handles()} in JHotDraw). L'intento GoF — delegare alle sottoclassi la
     * decisione su quale classe concreta istanziare — non richiede che il metodo restituisca un
     * singolo oggetto: è un dettaglio del diagramma canonico, non della definizione.
     *
     * <p>Per la variante a collezione, l'argomento generico deve essere leggibile dal bytecode
     * (attributo Signature): se il tipo di ritorno è una collezione raw (senza generics — il caso
     * di JHotDraw 1997, precedente ai generics Java), l'elemento non è recuperabile e il metodo
     * NON viene considerato un match. Un tipo raw è l'unico segnale disponibile qui (a differenza
     * dei campi Collection di Composite/Observer, dove altri controlli corroborano già il ruolo),
     * quindi accettarlo per default trasformerebbe "restituisce una qualunque collezione" in
     * "restituisce Product" — esattamente il falso positivo strutturale che il tool evita ovunque.
     */
    private boolean returnsProduct(MethodInfo m) {
        String returnType = m.getReturnTypeName();
        if (returnType.equals(product.getClassName())) {
            return true;
        }
        if (CollectionTypes.KNOWN.contains(returnType)) {
            String elementType = m.getGenericReturnElementTypeName();
            return elementType != null && TypeHierarchy.isAssignable(elementType, product.getClassName());
        }
        return false;
    }

    private void checkConcreteCreatorExtendsCreator(List<String> violations) {
        if (!TypeHierarchy.isAssignable(concreteCreator.getClassName(), creator.getClassName())) {
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
