package com.patternverifier.decorator.wrong;

import com.patternverifier.decorator.correct.TextComponent;

// VIOLAZIONE: non implementa TextComponent
public class MissingInterfaceDecorator {

    @SuppressWarnings("unused")
    private TextComponent wrapped;

    public MissingInterfaceDecorator(TextComponent wrapped) {
        this.wrapped = wrapped;
    }
}
