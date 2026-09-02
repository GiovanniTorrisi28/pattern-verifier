package com.patternverifier.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Annotazione sul ConcreteMediator (realizza il contratto del Mediator).
// mediatorInterface: il tipo astratto da cui i Colleague dipendono.
// colleagues:        i partecipanti coordinati, almeno 2. L'enumerazione esplicita è ciò che
//                    rende verificabile la proprietà negativa del pattern (nessun Colleague
//                    riferisce direttamente un altro Colleague) su un insieme finito e noto.
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GoFMediator {
    Class<?> mediatorInterface();
    Class<?>[] colleagues();
}
