package com.patternverifier.command.correct;

// Receiver GoF: contiene la logica reale dell'operazione di stampa.
// PrintCommand la delega invocando print() — il Receiver non conosce Command.
public class Printer {
    public void print(String message) {
        System.out.println(message);
    }
}
