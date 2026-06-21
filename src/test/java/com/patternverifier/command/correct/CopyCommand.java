package com.patternverifier.command.correct;

// Variante: naming perform() + metodo undo() (reversible command)
public class CopyCommand implements EditCommand {
    private final String text;

    public CopyCommand(String text) {
        this.text = text;
    }

    @Override
    public void perform() {
        System.out.println("copy: " + text);
    }

    @Override
    public void undo() {
        System.out.println("undo copy");
    }
}
