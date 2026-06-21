package com.patternverifier.proxy.wrong;

import com.patternverifier.proxy.correct.Image;

// VIOLAZIONE: implementa Image ma non ha un campo del tipo Subject/RealSubject
public class MissingFieldProxy implements Image {

    @Override
    public void display() {}
}
