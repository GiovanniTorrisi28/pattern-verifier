package com.patternverifier.composite.correct;

import com.patternverifier.annotations.GoFComposite;
import java.util.ArrayList;
import java.util.List;

@GoFComposite(component = FileSystemItem.class)
public class FileSystemDirectory implements FileSystemItem {

    private final String name;
    private List<FileSystemItem> children = new ArrayList<>();

    public FileSystemDirectory(String name) {
        this.name = name;
    }

    public void addChild(FileSystemItem item) {
        children.add(item);
    }

    public void removeChild(FileSystemItem item) {
        children.remove(item);
    }

    @Override
    public String getName() { return name; }

    @Override
    public long getSize() {
        return children.stream().mapToLong(FileSystemItem::getSize).sum();
    }
}
