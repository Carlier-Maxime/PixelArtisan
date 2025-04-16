package fr.metouais.pixelartisan.common.command.base;

import org.jetbrains.annotations.NotNull;

public interface CommandArguments {
    <V> V getArg(@NotNull String name, @NotNull Class<V> clazz)  throws CommandException;
    default <V> V getArg(@NotNull String name, @NotNull Class<V> clazz, V defaultValue) {
        try {
            return getArg(name, clazz);
        } catch (CommandException e) {
            return defaultValue;
        }
    }
    default CommandArgumentsWrapper wrapper() {
        return CommandArgumentsWrapper.of(this);
    }
    long size();
}
