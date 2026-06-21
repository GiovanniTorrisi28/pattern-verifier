package com.patternverifier.adapter.correct;

public class SocketAdapter implements ModernSocket {

    private LegacySocket legacySocket;

    public SocketAdapter(LegacySocket legacySocket) {
        this.legacySocket = legacySocket;
    }

    @Override
    public void connect(String host, int port) {
        legacySocket.open(host, port);
    }
}
