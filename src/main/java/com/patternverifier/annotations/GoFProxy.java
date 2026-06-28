package com.patternverifier.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Annotazione sul Proxy (implementa Subject, mantiene riferimento a RealSubject, delega).
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GoFProxy {
    Class<?> subject();
    Class<?> realSubject();
}
