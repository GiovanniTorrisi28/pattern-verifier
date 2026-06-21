package com.patternverifier.singleton.correct;

// Variante lazy: il campo è null finché non viene richiesta la prima istanza.
// Il getter ha nome diverso da getInstance() — il verifier deve accettarlo comunque.
public class LazySingleton {

    private static LazySingleton instance;

    private LazySingleton() {}

    public static LazySingleton get() {
        if (instance == null) {
            instance = new LazySingleton();
        }
        return instance;
    }
}
