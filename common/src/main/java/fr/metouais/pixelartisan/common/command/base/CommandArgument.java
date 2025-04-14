package fr.metouais.pixelartisan.common.command.base;

import java.util.List;

public interface CommandArgument<T> extends CommandNode {
    @Override
    CommandArgument<T> argument(CommandArgument<?> argument);
    @Override
    CommandArgument<T> execute(CommandExecutor executor);
    CommandArgument<T> suggests(List<T> suggests);
}
