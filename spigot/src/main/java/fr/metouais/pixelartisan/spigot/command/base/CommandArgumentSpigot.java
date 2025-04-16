package fr.metouais.pixelartisan.spigot.command.base;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import fr.metouais.pixelartisan.common.command.base.CommandArgument;
import fr.metouais.pixelartisan.common.command.base.CommandExecutor;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class CommandArgumentSpigot<T> implements CommandArgument<T> {
    private final Argument<?> argument;
    private CommandExecutor executor = null;
    private CommandArgument<?> child;

    private CommandArgumentSpigot(Argument<?> argument) {
        this.argument = argument;
        argument.setOptional(true);
    }

    public static <T> CommandArgumentSpigot<T> of(Argument<?> argument) {
        return new CommandArgumentSpigot<>(argument);
    }

    public Argument<?> getArgument() {
        return argument;
    }

    @Override
    public CommandArgumentSpigot<T> argument(CommandArgument<?> argument) {
        child = argument;
        return this;
    }

    @Override
    public CommandArgumentSpigot<T> execute(CommandExecutor executor) {
        this.executor = executor;
        return this;
    }

    @Override
    public final CommandArgument<T> suggests(List<T> suggests) {
        argument.replaceSuggestions(ArgumentSuggestions.strings(info -> suggestsToStringArray(suggests)));
        return this;
    }

    @Override
    public CommandArgument<T> suggests(Function<String, List<T>> suggestsProvider) {
        argument.replaceSuggestions(ArgumentSuggestions.strings(info -> suggestsToStringArray(suggestsProvider.apply(info.currentInput()))));
        return this;
    }

    private String[] suggestsToStringArray(List<T> suggests) {
        return suggests.stream().map(Objects::toString).toArray(String[]::new);
    }

    @Override
    public CommandExecutor getExecutor() {
        return executor;
    }

    @Override
    public CommandArgument<?> getChild() {
        return child;
    }

    @Override
    public String getName() {
        return argument.getNodeName();
    }
}
