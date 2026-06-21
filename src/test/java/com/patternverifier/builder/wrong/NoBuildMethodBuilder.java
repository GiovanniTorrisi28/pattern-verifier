package com.patternverifier.builder.wrong;

// VIOLAZIONE 2: ha metodi fluenti ma nessun metodo build*/create*/construct*
// che restituisca il Product
public class NoBuildMethodBuilder {
    private String name;

    public NoBuildMethodBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public String assemble() { return name; }
}
