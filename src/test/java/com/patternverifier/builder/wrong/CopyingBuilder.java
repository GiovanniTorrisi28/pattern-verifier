package com.patternverifier.builder.wrong;

import com.patternverifier.builder.correct.Person;

// VIOLAZIONE: i metodi dichiarano il tipo del Builder come tipo di ritorno — quindi la catena di
// chiamate compila — ma restituiscono una NUOVA istanza invece di this (stile builder immutabile).
// Il tipo di ritorno dichiarato non distingue questo caso da una fluent interface canonica:
// serve leggere il corpo del metodo (SelfReturnAnalyzer).
public class CopyingBuilder {
    private String name;
    private int age;

    public CopyingBuilder withName(String name) {
        CopyingBuilder next = new CopyingBuilder();
        next.name = name;
        next.age = this.age;
        return next;
    }

    public CopyingBuilder withAge(int age) {
        CopyingBuilder next = new CopyingBuilder();
        next.name = this.name;
        next.age = age;
        return next;
    }

    public Person build() {
        return new Person(name, age);
    }
}
