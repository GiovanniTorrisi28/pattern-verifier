package com.patternverifier.assertions;

import com.patternverifier.core.ClassAnalyzer;
import com.patternverifier.core.ClassMetadata;
import com.patternverifier.verifiers.CommandVerifier;

import java.util.List;

public class CommandAssert {

    private final Class<?> concreteCommandClass;
    private final ClassMetadata concreteCommandMetadata;
    private ClassMetadata commandMetadata;

    public CommandAssert(Class<?> clazz, ClassMetadata metadata) {
        this.concreteCommandClass = clazz;
        this.concreteCommandMetadata = metadata;
    }

    public CommandAssert withCommandInterface(Class<?> commandClass) {
        commandMetadata = ClassAnalyzer.analyze(commandClass);
        List<String> violations = new CommandVerifier(concreteCommandMetadata, commandMetadata).verify();
        if (!violations.isEmpty()) {
            throw new AssertionError(formatViolations(commandClass, violations));
        }
        return this;
    }

    public void withReceiver(Class<?> receiverClass) {
        if (commandMetadata == null) {
            throw new IllegalStateException("Chiamare withCommandInterface() prima di withReceiver()");
        }
        ClassMetadata receiverMetadata = ClassAnalyzer.analyze(receiverClass);
        List<String> violations = new CommandVerifier(concreteCommandMetadata, commandMetadata, receiverMetadata).verify();
        if (!violations.isEmpty()) {
            throw new AssertionError(formatViolations(receiverClass, violations));
        }
    }

    private String formatViolations(Class<?> secondClass, List<String> violations) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(concreteCommandClass.getSimpleName())
          .append(" / ").append(secondClass.getSimpleName())
          .append(": violazione pattern Command\n");
        violations.forEach(v -> sb.append("  - ").append(v).append("\n"));
        return sb.toString();
    }
}
