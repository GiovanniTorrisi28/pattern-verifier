package com.patternverifier.command.correct;

// Variante: naming perform() + metodo undo() (reversible command)
public class CopyCommand implements EditCommand {
    private final Clipboard clipboard;
    private final String text;

    public CopyCommand(Clipboard clipboard, String text) {
        this.clipboard = clipboard;
        this.text = text;
    }

    @Override
    public void perform() {
        clipboard.copy(text);
    }

    @Override
    public void undo() {
        clipboard.paste();
    }
}
