package com.patternverifier.command.wrong;

// VIOLAZIONE 2: interfaccia con metodo che non rispetta la naming convention
// (execute*, run*, perform*, invoke*) — doAction() non è riconosciuto
public interface WrongNamingCommand {
    void doAction();
}
