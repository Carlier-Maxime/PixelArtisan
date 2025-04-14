package fr.metouais.pixelartisan.spigot.command.base;

import dev.jorel.commandapi.arguments.Argument;
import fr.metouais.pixelartisan.common.command.base.CommandArgument;
import fr.metouais.pixelartisan.common.command.base.CommandExecutor;

public class CommandArgumentSpigot<T> implements CommandArgument<T> {
    private Argument<?> argument;

    private CommandArgumentSpigot(Argument<?> argument) {
        this.argument = argument;
    }

    public static <T> CommandArgumentSpigot<T> of(Argument<?> argument) {
        return new CommandArgumentSpigot<>(argument);
    }

    public Argument<?> getArgument() {
        return argument;
    }

    @Override
    public CommandArgumentSpigot<T> argument(CommandArgument<?> argument) {
        if (argument instanceof CommandArgumentSpigot<?> arg) this.argument.combineWith(arg.argument);
        return this;
    }

    @Override
    public CommandArgumentSpigot<T> execute(CommandExecutor executor) {
        argument = argument.executes(CommandSpigot.toCommandAPIExecutor(executor));
        return this;
    }
}
