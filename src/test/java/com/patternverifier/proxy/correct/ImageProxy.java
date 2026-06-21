package com.patternverifier.proxy.correct;

public class ImageProxy implements Image {

    private Image subject;

    public ImageProxy(Image subject) {
        this.subject = subject;
    }

    @Override
    public void display() {
        subject.display();
    }
}
