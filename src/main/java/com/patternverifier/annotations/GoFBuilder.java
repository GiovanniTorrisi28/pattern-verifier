package com.patternverifier.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Annotazione sul Builder (classe con metodi fluenti e metodo build*).
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GoFBuilder {
    Class<?> product();
}
