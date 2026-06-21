package com.patternverifier.singleton.wrong;

public class MissingInstanceFieldSingleton {

    // VIOLAZIONE: nessun campo static del proprio tipo

    private MissingInstanceFieldSingleton() {}

    public static MissingInstanceFieldSingleton getInstance() {
        return new MissingInstanceFieldSingleton(); // crea sempre una nuova istanza
    }
}
