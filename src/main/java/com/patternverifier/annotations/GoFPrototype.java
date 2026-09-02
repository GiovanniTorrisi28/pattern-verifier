package com.patternverifier.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Annotazione sul ConcretePrototype (si conforma al Prototype e sa produrre una copia di sé).
// prototype: il tipo che dichiara il contratto di clonazione.
// client:    opzionale — la classe che detiene il prototipo e ne invoca la clonazione. Solo
//            dichiarandola si verifica la proprietà di interazione (Livello 2) che distingue il
//            Prototype da una qualunque classe clonabile. Default void.class = non dichiarato.
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GoFPrototype {
    Class<?> prototype();
    Class<?> client() default void.class;
}
