package com.patternverifier.command.correct;

public class PrintCommand implements Command {
    private final String message;

    public PrintCommand(String message) {
        this.message = message;
    }

    @Override
    public void execute() {
        System.out.println(message);
    }
}
