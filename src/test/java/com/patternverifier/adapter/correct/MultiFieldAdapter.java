package com.patternverifier.adapter.correct;

// Variante: l'Adapter ha campi aggiuntivi oltre al riferimento all'Adaptee.
// Il verifier deve trovare il campo del tipo Adaptee anche in presenza di altri campi.
public class MultiFieldAdapter implements ModernSocket {

    private final String label;
    private final int timeout;
    private LegacySocket legacySocket;

    public MultiFieldAdapter(LegacySocket legacySocket, String label, int timeout) {
        this.legacySocket = legacySocket;
        this.label = label;
        this.timeout = timeout;
    }

    @Override
    public void connect(String host, int port) {
        System.out.println("[" + label + "] connecting with timeout=" + timeout);
        legacySocket.open(host, port);
    }
}
