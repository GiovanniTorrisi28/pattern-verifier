package com.patternverifier.adapter.wrong;

import com.patternverifier.adapter.correct.LegacySocket;
import com.patternverifier.adapter.correct.ModernSocket;

public class NoDelegatingAdapter implements ModernSocket {

    @SuppressWarnings("unused")
    private LegacySocket legacySocket;

    public NoDelegatingAdapter(LegacySocket legacySocket) {
        this.legacySocket = legacySocket;
    }

    @Override
    public void connect(String host, int port) {
        // non delega mai a legacySocket — non è un vero adattatore
    }
}
