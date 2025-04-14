package fr.metouais.pixelartisan.spigot.command.base;

import dev.jorel.commandapi.arguments.Argument;
import fr.metouais.pixelartisan.common.command.base.CommandArgument;

public class CommandArgumentSpigot<T> implements CommandArgument<T> {
    private final Argument<?> argument;

    private CommandArgumentSpigot(Argument<?> argument) {
        this.argument = argument;
    }

    public static <T> CommandArgumentSpigot<T> of(Argument<?> argument) {
        return new CommandArgumentSpigot<>(argument);
    }

    public Argument<?> getArgument() {
        return argument;
    }
}
