package com.patternverifier.decorator.wrong;

import com.patternverifier.decorator.correct.TextComponent;

// VIOLAZIONE: ha il campo corretto ma nessun costruttore che accetta TextComponent
public class MissingConstructorDecorator implements TextComponent {

    @SuppressWarnings("unused")
    private TextComponent wrapped;

    public MissingConstructorDecorator() {}

    public void setWrapped(TextComponent wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String render() {
        return "";
    }
}
