package com.patternverifier.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Annotazione sul ConcreteCommand.
// receiver è opzionale: se omesso (Void.class) il check di delegazione al Receiver viene saltato.
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GoFCommand {
    Class<?> commandInterface();
    Class<?> receiver() default Void.class;
}
