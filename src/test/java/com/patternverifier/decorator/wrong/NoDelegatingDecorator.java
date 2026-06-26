package com.patternverifier.decorator.wrong;

import com.patternverifier.decorator.correct.TextComponent;

// Violazione: struttura corretta (interfaccia + campo + costruttore) ma non delega mai al Component.
public class NoDelegatingDecorator implements TextComponent {

    @SuppressWarnings("unused")
    private TextComponent wrapped;

    public NoDelegatingDecorator(TextComponent wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String render() {
        // non chiama mai wrapped.render() — nessuna delega al Component
        return "hardcoded";
    }
}
