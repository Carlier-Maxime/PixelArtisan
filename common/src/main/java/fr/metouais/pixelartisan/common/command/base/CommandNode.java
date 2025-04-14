package fr.metouais.pixelartisan.common.command.base;

public interface CommandNode {
    CommandNode argument(CommandArgument<?> argument);
    CommandNode execute(CommandExecutor executor);
}
