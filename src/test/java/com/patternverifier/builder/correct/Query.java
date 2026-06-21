package com.patternverifier.builder.correct;

public class Query {
    @SuppressWarnings("unused")
    private final String table;
    @SuppressWarnings("unused")
    private final String condition;

    public Query(String table, String condition) {
        this.table = table;
        this.condition = condition;
    }
}
