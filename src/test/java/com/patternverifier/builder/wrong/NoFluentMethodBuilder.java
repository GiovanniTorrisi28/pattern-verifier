package com.patternverifier.builder.wrong;

import com.patternverifier.builder.correct.Person;

// VIOLAZIONE 1: i setter restituiscono void invece del tipo Builder —
// non supporta la catena di chiamate
public class NoFluentMethodBuilder {
    private String name;
    private int age;

    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }

    public Person build() { return new Person(name, age); }
}
