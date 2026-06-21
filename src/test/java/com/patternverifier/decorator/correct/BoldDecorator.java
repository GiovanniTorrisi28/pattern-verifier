package com.patternverifier.decorator.correct;

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
