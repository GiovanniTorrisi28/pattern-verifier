package com.patternverifier.composite.correct;

import java.util.HashSet;
import java.util.Set;

// Variante: usa Set invece di List, e il metodo si chiama addItem invece di addChild.
// Il verifier deve accettare qualsiasi tipo di Collection e qualsiasi nome che inizia con "add".
public class MenuGroup implements FileSystemItem {

    private final String name;
    private Set<FileSystemItem> items = new HashSet<>();

    public MenuGroup(String name) {
        this.name = name;
    }

    public void addItem(FileSystemItem item) {
        items.add(item);
    }

    @Override
    public String getName() { return name; }

    @Override
    public long getSize() {
        return items.stream().mapToLong(FileSystemItem::getSize).sum();
    }
}
