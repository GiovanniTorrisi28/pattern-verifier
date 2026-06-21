package com.patternverifier.adapter.wrong;

// VIOLAZIONE 1: non implementa ModernSocket (Target)
// VIOLAZIONE 2: nessun campo di tipo LegacySocket (Adaptee)
public class AllViolationsAdapter {

    @SuppressWarnings("unused")
    private String description;

    public AllViolationsAdapter(String description) {
        this.description = description;
    }
}
