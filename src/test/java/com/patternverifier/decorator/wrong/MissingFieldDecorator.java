package com.patternverifier.decorator.wrong;

import com.patternverifier.decorator.correct.TextComponent;

// VIOLAZIONE: implementa TextComponent ma non ha un campo del tipo interfaccia
public class MissingFieldDecorator implements TextComponent {

    public MissingFieldDecorator(TextComponent wrapped) {
        // non salva il riferimento al Component
    }

    @Override
    public String render() {
        return "";
    }
}
