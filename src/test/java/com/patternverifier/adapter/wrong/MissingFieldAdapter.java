package com.patternverifier.adapter.wrong;

import com.patternverifier.adapter.correct.ModernSocket;

public class MissingFieldAdapter implements ModernSocket {

    // VIOLAZIONE: nessun campo di tipo LegacySocket

    @Override
    public void connect(String host, int port) {}
}
