package com.patternverifier.builder.correct;

// Variante: naming convention create() invece di build(), metodi fluenti con nomi di dominio
public class QueryBuilder {
    private String table;
    private String condition;

    public QueryBuilder from(String table) {
        this.table = table;
        return this;
    }

    public QueryBuilder where(String condition) {
        this.condition = condition;
        return this;
    }

    public Query create() {
        return new Query(table, condition);
    }
}
