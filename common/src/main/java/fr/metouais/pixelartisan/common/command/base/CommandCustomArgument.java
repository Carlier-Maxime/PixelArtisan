package fr.metouais.pixelartisan.common.command.base;

import java.util.List;
import java.util.function.Function;

public class CommandCustomArgument<S, T> implements CommandArgument<T> {
    private final CommandArgument<S> baseArgument;
    private final Function<S, T> toT;
    private final Function<T, S> toS;
    private final Class<S> clazzS;

    public CommandCustomArgument(CommandArgument<S> baseArgument, Function<S, T> toT, Function<T, S> toS, Class<S> clazzS) {
        this.baseArgument = baseArgument;
        this.toT = toT;
        this.toS = toS;
        this.clazzS = clazzS;
    }

    @Override
    public CommandArgument<T> argument(CommandArgument<?> argument) {
        var child = argument;
        while (child != null) {
            final var executor = child.getExecutor();
            child.execute((ctx) -> {
                ctx.getArguments().addCustomCast(getName(), toT, clazzS);
                if (executor != null) executor.exec(ctx);
                else throw new CommandException("Incomplete or incorrect command");
            });
            child = child.getChild();
        }
        baseArgument.argument(argument);
        return this;
    }

    @Override
    public CommandArgument<T> execute(CommandExecutor executor) {
        baseArgument.execute((ctx) -> {
            ctx.getArguments().addCustomCast(getName(), toT, clazzS);
            executor.exec(ctx);
        });
        return this;
    }

    @Override
    public CommandArgument<T> suggests(List<T> suggests) {
        baseArgument.suggests(suggests.stream().map(toS).toList());
        return this;
    }

    @Override
    public CommandArgument<T> suggests(Function<String, List<T>> suggestsProvider) {
        baseArgument.suggests(input -> suggestsProvider.apply(input).stream().map(toS).toList());
        return this;
    }

    @Override
    public String getName() {
        return baseArgument.getName();
    }

    @Override
    public CommandExecutor getExecutor() {
        return baseArgument.getExecutor();
    }

    @Override
    public CommandArgument<?> getChild() {
        return baseArgument.getChild();
    }

    public CommandArgument<S> getBase() {
        return baseArgument;
    }
}
