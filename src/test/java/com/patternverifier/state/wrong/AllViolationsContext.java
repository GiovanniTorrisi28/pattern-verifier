package com.patternverifier.state.wrong;

// VIOLAZIONI 3+4: nessun campo State, nessun metodo di transizione
// Usato assieme a ConcreteState (violazione 1) per testare violazioni multiple
public class AllViolationsContext {

    private String label;

    public AllViolationsContext(String label) {
        this.label = label;
    }

    public void doWork() {
        System.out.println(label);
    }
}
