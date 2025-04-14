package fr.metouais.pixelartisan.spigot.command.base;

import dev.jorel.commandapi.arguments.Argument;
import fr.metouais.pixelartisan.common.command.base.CommandArgument;
import fr.metouais.pixelartisan.common.command.base.CommandExecutor;

public class CommandArgumentSpigot<T> implements CommandArgument<T> {
    private final Argument<?> argument;
    private CommandExecutor executor;
    private CommandArgumentSpigot<?> child;

    private CommandArgumentSpigot(Argument<?> argument) {
        this.argument = argument;
        argument.setOptional(true);
    }

    public static <T> CommandArgumentSpigot<T> of(Argument<?> argument) {
        return new CommandArgumentSpigot<>(argument);
    }

    public Argument<?> getArgument() {
        return argument;
    }

    @Override
    public CommandArgumentSpigot<T> argument(CommandArgument<?> argument) {
        if (argument instanceof CommandArgumentSpigot<?> arg) child = arg;
        else throw new IllegalArgumentException("Argument is not a "+CommandArgumentSpigot.class.getSimpleName());
        return this;
    }

    @Override
    public CommandArgumentSpigot<T> execute(CommandExecutor executor) {
        this.executor = executor;
        return this;
    }

    public CommandExecutor getExecutor() {
        return executor;
    }

    public CommandArgumentSpigot<?> getChild() {
        return child;
    }
}
