package com.patternverifier.decorator.correct;

// Variante: Decorator con campi aggiuntivi oltre al Component.
// Il verifier deve trovare il campo TextComponent anche in presenza di altri campi.
public class ItalicDecorator implements TextComponent {

    private TextComponent wrapped;
    private final String cssClass;

    public ItalicDecorator(TextComponent wrapped, String cssClass) {
        this.wrapped = wrapped;
        this.cssClass = cssClass;
    }

    @Override
    public String render() {
        return "<i class=\"" + cssClass + "\">" + wrapped.render() + "</i>";
    }
}
