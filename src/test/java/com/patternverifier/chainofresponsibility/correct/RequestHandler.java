package com.patternverifier.chainofresponsibility.correct;

import com.patternverifier.annotations.GoFChainOfResponsibility;

@GoFChainOfResponsibility
public abstract class RequestHandler {

    protected RequestHandler next;

    public void setNext(RequestHandler next) {
        this.next = next;
    }

    public abstract void handleRequest(String request);
}
