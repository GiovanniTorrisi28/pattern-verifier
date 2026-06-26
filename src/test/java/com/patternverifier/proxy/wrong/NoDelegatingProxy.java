package com.patternverifier.proxy.wrong;

import com.patternverifier.proxy.correct.Image;

// Violazione: struttura corretta (interfaccia + campo) ma non delega mai al Subject.
public class NoDelegatingProxy implements Image {

    @SuppressWarnings("unused")
    private Image subject;

    public NoDelegatingProxy(Image subject) {
        this.subject = subject;
    }

    @Override
    public void display() {
        // non chiama mai subject.display() — nessuna delega al Subject
    }
}
