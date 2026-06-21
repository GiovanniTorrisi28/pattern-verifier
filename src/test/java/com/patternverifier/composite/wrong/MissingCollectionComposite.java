package com.patternverifier.composite.wrong;

import com.patternverifier.composite.correct.FileSystemItem;

// VIOLAZIONE: implementa il Component ma non ha un campo di tipo Collection
public class MissingCollectionComposite implements FileSystemItem {

    private final String name;

    public MissingCollectionComposite(String name) {
        this.name = name;
    }

    public void addChild(FileSystemItem item) {}

    @Override
    public String getName() { return name; }

    @Override
    public long getSize() { return 0; }
}
