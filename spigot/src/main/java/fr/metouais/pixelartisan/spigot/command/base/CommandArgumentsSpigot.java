package fr.metouais.pixelartisan.spigot.command.base;

import fr.metouais.pixelartisan.common.command.base.CommandArguments;
import fr.metouais.pixelartisan.common.command.base.CommandException;
import org.jetbrains.annotations.NotNull;

public class CommandArgumentsSpigot implements CommandArguments {
    private final dev.jorel.commandapi.executors.CommandArguments arguments;

    private CommandArgumentsSpigot(@NotNull dev.jorel.commandapi.executors.CommandArguments arguments) {
        this.arguments = arguments;
    }

    public static CommandArguments of(@NotNull dev.jorel.commandapi.executors.CommandArguments arguments) {
        return new CommandArgumentsSpigot(arguments);
    }

    @Override
    public <V> V getArg(@NotNull String name, @NotNull Class<V> clazz) {
        var arg = arguments.get(name);
        if (clazz.isInstance(arg)) return clazz.cast(arg);
        else throw new CommandException("Argument " + name + " is not of type " + clazz);
    }

    @Override
    public long size() {
        return arguments.args().length;
    }
}
