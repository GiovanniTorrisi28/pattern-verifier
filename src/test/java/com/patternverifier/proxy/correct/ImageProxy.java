package com.patternverifier.proxy.correct;

import com.patternverifier.annotations.GoFProxy;

@GoFProxy(subject = Image.class, realSubject = RealImage.class)
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
