package com.patternverifier.command.correct;

// Variante: nessun Receiver separato — la logica dell'operazione è contenuta nel comando
// stesso (self-contained command), un caso GoF legittimo che non ha nulla su cui delegare.
public class SelfContainedCommand implements Command {

    @SuppressWarnings("unused")
    private int counter = 0;

    @Override
    public void execute() {
        counter++;
    }
}
