package com.patternverifier.bridge.wrong;

import com.patternverifier.bridge.correct.Device;

public class NoDelegatingAbstraction {

    @SuppressWarnings("unused")
    protected Device device;

    public NoDelegatingAbstraction(Device device) {
        this.device = device;
    }

    public void toggle() {
        // non invoca mai metodi su device — non delega all'Implementor
    }
}
