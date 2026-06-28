package com.patternverifier.singleton.correct;

import com.patternverifier.annotations.GoFSingleton;

@GoFSingleton
public class DatabaseConnection {

    private static final DatabaseConnection INSTANCE = new DatabaseConnection();

    private DatabaseConnection() {}

    public static DatabaseConnection getInstance() {
        return INSTANCE;
    }
}
