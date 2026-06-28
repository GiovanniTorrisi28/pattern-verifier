package com.patternverifier.templatemethod.correct;

import com.patternverifier.annotations.GoFTemplateMethod;

@GoFTemplateMethod(templateMethod = "process")
public abstract class DataProcessor {

    public final void process() {
        readData();
        processData();
        writeData();
    }

    protected abstract void readData();
    protected abstract void processData();
    protected abstract void writeData();
}
