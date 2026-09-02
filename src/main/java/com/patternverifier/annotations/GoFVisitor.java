package com.patternverifier.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Annotazione sul ConcreteVisitor (implementa l'interfaccia Visitor, ha metodi visit* concreti).
// element:         il tipo Element che dichiara accept(Visitor).
// concreteElement: opzionale — la classe concreta che implementa accept. Dichiararla è l'unico
//                  modo di far verificare il double dispatch quando Element è un'interfaccia
//                  (il caso canonico): il corpo di accept vive solo in una classe concreta.
//                  Se omesso (default void.class) il controllo comportamentale viene eseguito
//                  su Element solo se essa stessa è concreta, altrimenti saltato.
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GoFVisitor {
    Class<?> visitorInterface();
    Class<?> element();
    Class<?> concreteElement() default void.class;
}
