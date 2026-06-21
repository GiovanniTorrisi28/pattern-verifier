package com.patternverifier.adapter.wrong;

import com.patternverifier.adapter.correct.ModernSocket;

// VIOLAZIONE: l'Adaptee implementa già il Target — l'Adapter è inutile
public class AdapteeAlreadyImplementsTarget implements ModernSocket {

    @Override
    public void connect(String host, int port) {}
}
