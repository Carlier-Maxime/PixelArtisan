package fr.metouais.pixelartisan.common.command.base;

public interface CommandArgument<T> extends CommandNode {
    @Override
    CommandArgument<T> argument(CommandArgument<?> argument);
    @Override
    CommandArgument<T> execute(CommandExecutor executor);
}
