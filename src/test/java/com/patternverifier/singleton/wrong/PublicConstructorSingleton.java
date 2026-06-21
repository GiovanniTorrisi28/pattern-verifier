package com.patternverifier.singleton.wrong;

public class PublicConstructorSingleton {

    private static final PublicConstructorSingleton INSTANCE = new PublicConstructorSingleton();

    public PublicConstructorSingleton() {} // VIOLAZIONE: deve essere private

    public static PublicConstructorSingleton getInstance() {
        return INSTANCE;
    }
}
