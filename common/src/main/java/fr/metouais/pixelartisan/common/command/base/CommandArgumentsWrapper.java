package fr.metouais.pixelartisan.common.command.base;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CommandArgumentsWrapper implements CommandArguments {
    private final CommandArguments args;
    private record Pair<A, B>(Function<A, B> func, Class<A> inputClass) {
        @SuppressWarnings("unchecked")
        public <V> V castArg(String name, CommandArguments args)  throws CommandException {
            return (V) func.apply(args.getArg(name, inputClass));
        }
    }
    private final Map<String, Pair<?, ?>> customCasts = new HashMap<>();
    private CommandArgumentsWrapper(CommandArguments args) {
        this.args = args;
    }
    public static CommandArgumentsWrapper of(CommandArguments args) {
        return new CommandArgumentsWrapper(args);
    }
    public <V> V getArg(@NotNull String name, @NotNull Class<V> clazz) throws CommandException {
        var cast = customCasts.get(name);
        return (cast == null) ? args.getArg(name, clazz) : cast.castArg(name, args);
    }
    public <A, B> void addCustomCast(String name, Function<A, B> func, Class<A> inputClass) {
        customCasts.put(name, new Pair<>(func, inputClass));
    }
}
