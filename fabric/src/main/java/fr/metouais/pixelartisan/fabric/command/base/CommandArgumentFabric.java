package fr.metouais.pixelartisan.fabric.command.base;

import com.mojang.brigadier.builder.ArgumentBuilder;
import fr.metouais.pixelartisan.common.command.base.CommandArgument;
import net.minecraft.server.command.ServerCommandSource;

public class CommandArgumentFabric<T> implements CommandArgument<T> {
    private final ArgumentBuilder<ServerCommandSource, ?> argBuilder;

    private CommandArgumentFabric(ArgumentBuilder<ServerCommandSource, ?> argBuilder) {
        this.argBuilder = argBuilder;
    }

    public static <T> CommandArgumentFabric<T> of(ArgumentBuilder<ServerCommandSource, ?> argBuilder){
        return new CommandArgumentFabric<>(argBuilder);
    }

    public ArgumentBuilder<ServerCommandSource, ?> getArgBuilder() {
        return argBuilder;
    }
}
