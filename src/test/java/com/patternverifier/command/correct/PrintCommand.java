package com.patternverifier.command.correct;

import com.patternverifier.annotations.GoFCommand;

@GoFCommand(commandInterface = Command.class, receiver = Printer.class)
public class PrintCommand implements Command {
    private final Printer printer;
    private final String message;

    public PrintCommand(Printer printer, String message) {
        this.printer = printer;
        this.message = message;
    }

    @Override
    public void execute() {
        printer.print(message);
    }
}
