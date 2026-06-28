package com.patternverifier.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Annotazione sul ConcreteVisitor (implementa l'interfaccia Visitor, ha metodi visit* concreti).
// element: il tipo Element su cui viene verificato il double dispatch (accept chiama visitor.visit).
//          Se Element è astratto o interfaccia il check comportamentale viene saltato automaticamente.
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GoFVisitor {
    Class<?> visitorInterface();
    Class<?> element();
}
