package com.patternverifier.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Annotazione sul ConcreteCreator — il Creator e il Product sono attributi dichiarati.
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GoFFactoryMethod {
    Class<?> creator();
    Class<?> product();
    String factoryMethod();
}
