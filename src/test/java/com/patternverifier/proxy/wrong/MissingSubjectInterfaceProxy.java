package com.patternverifier.proxy.wrong;

import com.patternverifier.proxy.correct.Image;

// VIOLAZIONE: non implementa Image (Subject)
public class MissingSubjectInterfaceProxy {

    @SuppressWarnings("unused")
    private Image subject;

    public MissingSubjectInterfaceProxy(Image subject) {
        this.subject = subject;
    }
}
