package com.patternverifier.adapter.wrong;

import com.patternverifier.adapter.correct.LegacySocket;

// VIOLAZIONE: non implementa ModernSocket (Target)
public class MissingInterfaceAdapter {

    @SuppressWarnings("unused")
    private LegacySocket legacySocket;

    public MissingInterfaceAdapter(LegacySocket legacySocket) {
        this.legacySocket = legacySocket;
    }
}
