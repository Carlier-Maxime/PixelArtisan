package fr.metouais.pixelartisan.fabric.command.base;

import fr.metouais.pixelartisan.common.command.base.CommandArguments;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;

public class CommandArgumentsFabric implements CommandArguments {
    private final CommandContext<ServerCommandSource> ctx;

    private CommandArgumentsFabric(@NotNull CommandContext<ServerCommandSource> ctx) {
        this.ctx = ctx;
    }

    public static CommandArgumentsFabric of(@NotNull CommandContext<ServerCommandSource> ctx) {
        return new CommandArgumentsFabric(ctx);
    }

    @Override
    public <V> V getArg(@NotNull String name, @NotNull Class<V> clazz) {
        return ctx.getArgument(name, clazz);
    }
}
