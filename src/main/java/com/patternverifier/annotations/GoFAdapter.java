package com.patternverifier.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Annotazione sull'Adapter (object adapter: implementa Target, ha campo Adaptee, delega).
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GoFAdapter {
    Class<?> adaptee();
    Class<?> target();
}
