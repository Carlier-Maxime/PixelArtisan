package fr.metouais.pixelartisan.common.command.base;

import org.jetbrains.annotations.NotNull;

public interface CommandArguments {
    <V> V getArg(@NotNull String name, @NotNull Class<V> clazz)  throws CommandException;
    default CommandArgumentsWrapper wrapper() {
        return CommandArgumentsWrapper.of(this);
    }
}
