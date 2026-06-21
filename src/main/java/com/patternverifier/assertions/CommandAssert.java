package com.patternverifier.assertions;

import com.patternverifier.core.ClassAnalyzer;
import com.patternverifier.core.ClassMetadata;
import com.patternverifier.verifiers.CommandVerifier;

import java.util.List;

public class CommandAssert {

    private final Class<?> concreteCommandClass;
    private final ClassMetadata concreteCommandMetadata;

    public CommandAssert(Class<?> clazz, ClassMetadata metadata) {
        this.concreteCommandClass = clazz;
        this.concreteCommandMetadata = metadata;
    }

    public void withCommandInterface(Class<?> commandClass) {
        ClassMetadata commandMetadata = ClassAnalyzer.analyze(commandClass);
        List<String> violations = new CommandVerifier(concreteCommandMetadata, commandMetadata).verify();
        if (!violations.isEmpty()) {
            throw new AssertionError(formatViolations(commandClass, violations));
        }
    }

    private String formatViolations(Class<?> commandClass, List<String> violations) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(concreteCommandClass.getSimpleName())
          .append(" / ").append(commandClass.getSimpleName())
          .append(": violazione pattern Command\n");
        violations.forEach(v -> sb.append("  - ").append(v).append("\n"));
        return sb.toString();
    }
}
