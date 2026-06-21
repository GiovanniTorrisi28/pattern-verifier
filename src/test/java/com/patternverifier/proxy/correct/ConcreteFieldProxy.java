package com.patternverifier.proxy.correct;

// Variante: il campo è del tipo concreto RealImage anziché dell'interfaccia Image.
// Il verifier accetta entrambe le forme (campo di tipo Subject o di tipo RealSubject).
public class ConcreteFieldProxy implements Image {

    private RealImage realSubject;

    public ConcreteFieldProxy(RealImage realSubject) {
        this.realSubject = realSubject;
    }

    @Override
    public void display() {
        realSubject.display();
    }
}
