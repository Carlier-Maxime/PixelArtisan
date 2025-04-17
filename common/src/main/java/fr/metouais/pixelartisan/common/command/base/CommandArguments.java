package fr.metouais.pixelartisan.common.command.base;

import org.jetbrains.annotations.NotNull;

public interface CommandArguments {
    <V> V getArg(@NotNull String name, @NotNull Class<V> clazz)  throws CommandException;
    default <V> V getArg(@NotNull String name, @NotNull Class<V> clazz, V defaultValue) {
        try {
            return getArg(name, clazz);
        } catch (Exception e) {
            return defaultValue;
        }
    }
    default CommandArgumentsWrapper wrapper(CommandContext ctx) {
        return CommandArgumentsWrapper.of(ctx, this);
    }
    long size();
}
