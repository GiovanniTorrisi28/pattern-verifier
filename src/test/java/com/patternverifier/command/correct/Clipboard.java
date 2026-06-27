package com.patternverifier.command.correct;

// Receiver GoF per CopyCommand: gestisce le operazioni di copia/incolla.
public class Clipboard {
    private String content;

    public void copy(String text) {
        this.content = text;
    }

    public void paste() {
        System.out.println("paste: " + content);
    }
}
