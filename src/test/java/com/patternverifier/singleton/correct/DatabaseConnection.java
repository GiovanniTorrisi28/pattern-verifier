package com.patternverifier.singleton.correct;

public class DatabaseConnection {

    private static final DatabaseConnection INSTANCE = new DatabaseConnection();

    private DatabaseConnection() {}

    public static DatabaseConnection getInstance() {
        return INSTANCE;
    }
}
