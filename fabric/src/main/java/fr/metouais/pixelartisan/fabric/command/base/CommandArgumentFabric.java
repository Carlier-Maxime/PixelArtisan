package fr.metouais.pixelartisan.fabric.command.base;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fr.metouais.pixelartisan.common.command.base.CommandArgument;
import fr.metouais.pixelartisan.common.command.base.CommandCustomArgument;
import fr.metouais.pixelartisan.common.command.base.CommandExecutor;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class CommandArgumentFabric<T> implements CommandArgument<T> {
    private RequiredArgumentBuilder<ServerCommandSource, ?> argBuilder;
    private CommandExecutor executor = null;
    private CommandArgumentFabric<?> child = null;

    private CommandArgumentFabric(RequiredArgumentBuilder<ServerCommandSource, ?> argBuilder) {
        this.argBuilder = argBuilder;
    }

    public static <T> CommandArgumentFabric<T> of(RequiredArgumentBuilder<ServerCommandSource, ?> argBuilder){
        return new CommandArgumentFabric<>(argBuilder);
    }

    public ArgumentBuilder<ServerCommandSource, ?> getArgBuilder() {
        return argBuilder;
    }

    @Override
    public CommandArgumentFabric<T> argument(CommandArgument<?> argument) {
        if (argument instanceof CommandArgumentFabric<?> arg) return argument(arg);
        else if (argument instanceof CommandCustomArgument<?,?> argCustom) return argument(argCustom.getBase());
        else throw new IllegalArgumentException("Argument is not a "+CommandArgumentFabric.class.getSimpleName());
    }

    private CommandArgumentFabric<T> argument(CommandArgumentFabric<?> argFabric) {
        argBuilder = argBuilder.then(argFabric.argBuilder);
        child = argFabric;
        return this;
    }

    @Override
    public CommandArgumentFabric<T> execute(CommandExecutor executor) {
        argBuilder = argBuilder.executes(CommandFabric.toBrigadierExecutor(executor));
        this.executor = executor;
        return this;
    }

    @Override
    public final CommandArgumentFabric<T> suggests(List<T> suggests) {
        argBuilder = argBuilder.suggests((ctx, builder) -> buildSuggests(builder, suggests));
        return this;
    }

    @Override
    public CommandArgumentFabric<T> suggests(Function<String, List<T>> suggestsProvider) {
        argBuilder = argBuilder.suggests((ctx, builder) -> buildSuggests(builder, suggestsProvider.apply(builder.getRemaining())));
        return this;
    }

    private CompletableFuture<Suggestions> buildSuggests(SuggestionsBuilder builder, List<T> suggests) {
        String input = builder.getRemaining();
        for (T suggest : suggests) if (suggest.toString().startsWith(input)) builder.suggest(suggest.toString());
        return builder.buildFuture();
    }

    @Override
    public String getName() {
        return argBuilder.getName();
    }

    @Override
    public CommandExecutor getExecutor() {
        return executor;
    }

    @Override
    public CommandArgumentFabric<?> getChild() {
        return child;
    }
}
