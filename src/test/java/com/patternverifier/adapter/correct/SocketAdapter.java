package com.patternverifier.adapter.correct;

import com.patternverifier.annotations.GoFAdapter;

@GoFAdapter(adaptee = LegacySocket.class, target = ModernSocket.class)
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
