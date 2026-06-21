package com.patternverifier.composite.wrong;

import com.patternverifier.composite.correct.FileSystemItem;

import java.util.ArrayList;
import java.util.List;

// VIOLAZIONE: non implementa FileSystemItem (Component)
public class MissingInterfaceComposite {

    private List<FileSystemItem> children = new ArrayList<>();

    public void addChild(FileSystemItem item) {
        children.add(item);
    }
}
