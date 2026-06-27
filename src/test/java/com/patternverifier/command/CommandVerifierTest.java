package com.patternverifier.command;

import com.patternverifier.PatternAssertions;
import com.patternverifier.command.correct.Clipboard;
import com.patternverifier.command.correct.Command;
import com.patternverifier.command.correct.CopyCommand;
import com.patternverifier.command.correct.EditCommand;
import com.patternverifier.command.correct.PrintCommand;
import com.patternverifier.command.correct.Printer;
import com.patternverifier.command.wrong.AbstractConcreteCommand;
import com.patternverifier.command.wrong.AllViolationsCommand;
import com.patternverifier.command.wrong.AllViolationsConcreteCommand;
import com.patternverifier.command.wrong.ConcreteCommandClass;
import com.patternverifier.command.wrong.NoReceiverCommand;
import com.patternverifier.command.wrong.WrongNamingCommand;
import com.patternverifier.command.wrong.WrongParentConcreteCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandVerifierTest {

    @Test
    void printCommandShouldPass() {
        PatternAssertions.assertThat(PrintCommand.class)
                .implementsCommand()
                .withCommandInterface(Command.class)
                .withReceiver(Printer.class);
    }

    @Test
    void copyCommandWithPerformAndUndoShouldPass() {
        // Variante: naming perform() + metodo undo() (reversible command)
        PatternAssertions.assertThat(CopyCommand.class)
                .implementsCommand()
                .withCommandInterface(EditCommand.class)
                .withReceiver(Clipboard.class);
    }

    @Test
    void concreteCommandClassShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(PrintCommand.class)
                        .implementsCommand()
                        .withCommandInterface(ConcreteCommandClass.class)
        );
        assertTrue(error.getMessage().contains("astratt") || error.getMessage().contains("interfaccia"),
                "Il messaggio dovrebbe indicare che il Command non è astratto");
    }

    @Test
    void wrongNamingCommandShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(PrintCommand.class)
                        .implementsCommand()
                        .withCommandInterface(WrongNamingCommand.class)
        );
        assertTrue(error.getMessage().contains("naming") || error.getMessage().contains("execute"),
                "Il messaggio dovrebbe indicare la naming convention mancante nel Command");
    }

    @Test
    void wrongParentConcreteCommandShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(WrongParentConcreteCommand.class)
                        .implementsCommand()
                        .withCommandInterface(Command.class)
        );
        assertTrue(error.getMessage().contains("implementa") || error.getMessage().contains("estende"),
                "Il messaggio dovrebbe indicare che il ConcreteCommand non implementa Command");
    }

    @Test
    void abstractConcreteCommandShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(AbstractConcreteCommand.class)
                        .implementsCommand()
                        .withCommandInterface(Command.class)
        );
        assertTrue(error.getMessage().contains("concreta") || error.getMessage().contains("implementazione"),
                "Il messaggio dovrebbe indicare che manca l'implementazione concreta di execute");
    }

    @Test
    void noReceiverCommandShouldBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(NoReceiverCommand.class)
                        .implementsCommand()
                        .withCommandInterface(Command.class)
                        .withReceiver(Printer.class)
        );
        assertTrue(error.getMessage().contains("Receiver") || error.getMessage().contains("delega"),
                "Il messaggio dovrebbe indicare che il ConcreteCommand non delega al Receiver");
    }

    @Test
    void multipleViolationsShouldAllBeReported() {
        AssertionError error = assertThrows(AssertionError.class, () ->
                PatternAssertions.assertThat(AllViolationsConcreteCommand.class)
                        .implementsCommand()
                        .withCommandInterface(AllViolationsCommand.class)
        );
        String msg = error.getMessage();
        assertTrue(msg.contains("astratt") || msg.contains("interfaccia"),
                "Dovrebbe riportare che il Command non è astratto");
        assertTrue(msg.contains("naming") || msg.contains("execute"),
                "Dovrebbe riportare la naming convention mancante nel Command");
        assertTrue(msg.contains("implementa") || msg.contains("estende"),
                "Dovrebbe riportare che il ConcreteCommand non implementa Command");
        assertTrue(msg.contains("concreta") || msg.contains("implementazione"),
                "Dovrebbe riportare che manca l'implementazione concreta di execute");
    }
}
