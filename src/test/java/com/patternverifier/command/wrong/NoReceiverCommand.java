package com.patternverifier.command.wrong;

import com.patternverifier.command.correct.Command;

// Strutturalmente corretto (implementa Command, ha execute()) ma VIOLAZIONE GoF:
// la logica è inline — non c'è un Receiver separato a cui delegare.
public class NoReceiverCommand implements Command {
    private final String message;

    public NoReceiverCommand(String message) {
        this.message = message;
    }

    @Override
    public void execute() {
        System.out.println(message);
    }
}
