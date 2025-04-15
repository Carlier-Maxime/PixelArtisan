package fr.metouais.pixelartisan.common.command.base;

import java.util.List;
import java.util.function.Function;

public interface CommandArgument<T> extends CommandNode {
    @Override
    CommandArgument<T> argument(CommandArgument<?> argument);
    @Override
    CommandArgument<T> execute(CommandExecutor executor);
    CommandArgument<T> suggests(List<T> suggests);
    CommandArgument<T> suggests(Function<String, List<T>> suggestsProvider);
    String getName();
    CommandExecutor getExecutor();
    CommandArgument<?> getChild();
}
