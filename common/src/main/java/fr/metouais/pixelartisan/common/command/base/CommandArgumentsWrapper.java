package fr.metouais.pixelartisan.common.command.base;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class CommandArgumentsWrapper implements CommandArguments {
    private final CommandContext ctx;
    private final CommandArguments args;
    private record Pair<A, B>(BiFunction<CommandContext, A, B> func, Class<A> inputClass) {
        @SuppressWarnings("unchecked")
        public <V> V castArg(CommandContext ctx, String name, CommandArguments args)  throws CommandException {
            return (V) func.apply(ctx, args.getArg(name, inputClass));
        }
    }
    private final Map<String, Pair<?, ?>> customCasts = new HashMap<>();
    private CommandArgumentsWrapper(CommandContext ctx, CommandArguments args) {
        this.ctx = ctx;
        this.args = args;
    }
    public static CommandArgumentsWrapper of(CommandContext ctx, CommandArguments args) {
        return new CommandArgumentsWrapper(ctx, args);
    }
    public <V> V getArg(@NotNull String name, @NotNull Class<V> clazz) throws CommandException {
        var cast = customCasts.get(name);
        return (cast == null) ? args.getArg(name, clazz) : cast.castArg(ctx, name, args);
    }
    public <A, B> void addCustomCast(String name, BiFunction<CommandContext, A, B> func, Class<A> inputClass) {
        customCasts.put(name, new Pair<>(func, inputClass));
    }

    @Override
    public long size() {
        return args.size();
    }
}
