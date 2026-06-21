package com.patternverifier.singleton.wrong;

public class MissingGetterSingleton {

    @SuppressWarnings("unused")
    private static final MissingGetterSingleton INSTANCE = new MissingGetterSingleton();

    private MissingGetterSingleton() {}

    // VIOLAZIONE: nessun metodo static che restituisce MissingGetterSingleton
    public void doSomething() {}
}
