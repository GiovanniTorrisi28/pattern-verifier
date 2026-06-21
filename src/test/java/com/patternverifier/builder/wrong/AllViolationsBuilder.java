package com.patternverifier.builder.wrong;

// Usato nel test delle violazioni multiple.
// VIOLAZIONE 1: nessun metodo restituisce il tipo Builder
// VIOLAZIONE 2: nessun metodo build*/create*/construct* che restituisca il Product
public class AllViolationsBuilder {
    public void setSomething(String value) {}
    public String getSomething() { return ""; }
}
