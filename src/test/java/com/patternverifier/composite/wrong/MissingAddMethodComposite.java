package com.patternverifier.composite.wrong;

import com.patternverifier.composite.correct.FileSystemItem;

import java.util.ArrayList;
import java.util.List;

// VIOLAZIONE: ha la Collection ma nessun metodo add*(FileSystemItem)
public class MissingAddMethodComposite implements FileSystemItem {

    private final String name;
    @SuppressWarnings("unused")
    private List<FileSystemItem> children = new ArrayList<>();

    public MissingAddMethodComposite(String name) {
        this.name = name;
    }

    // nessun metodo add* che accetta FileSystemItem

    @Override
    public String getName() { return name; }

    @Override
    public long getSize() { return 0; }
}
