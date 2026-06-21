package com.patternverifier.command.correct;

// Variante: naming convention perform() invece di execute()
public interface EditCommand {
    void perform();
    void undo();
}
