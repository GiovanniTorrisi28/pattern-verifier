package com.patternverifier.composite.wrong;

import com.patternverifier.composite.correct.FileSystemItem;

import java.util.ArrayList;
import java.util.List;

// Struttura corretta: implementa Component, ha Collection, ha addChild.
// Violazione: non invoca mai metodi sui FileSystemItem figli.
public class NoDelegatingComposite implements FileSystemItem {

    private List<FileSystemItem> children = new ArrayList<>();

    public void addChild(FileSystemItem item) {
        children.add(item);
    }

    @Override
    public String getName() { return "composite"; }

    @Override
    public long getSize() { return 0; }
}
