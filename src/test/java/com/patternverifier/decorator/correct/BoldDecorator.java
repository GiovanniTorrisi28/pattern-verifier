package com.patternverifier.decorator.correct;

import com.patternverifier.annotations.GoFDecorator;

@GoFDecorator(component = TextComponent.class)
public class BoldDecorator implements TextComponent {

    private TextComponent wrapped;

    public BoldDecorator(TextComponent wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String render() {
        return "<b>" + wrapped.render() + "</b>";
    }
}
