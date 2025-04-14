package fr.metouais.pixelartisan.fabric.command.base;

import com.mojang.brigadier.builder.ArgumentBuilder;
import fr.metouais.pixelartisan.common.command.base.CommandArgument;
import fr.metouais.pixelartisan.common.command.base.CommandExecutor;
import fr.metouais.pixelartisan.common.command.base.CommandNode;
import net.minecraft.server.command.ServerCommandSource;

public class CommandArgumentFabric<T> implements CommandArgument<T> {
    private ArgumentBuilder<ServerCommandSource, ?> argBuilder;

    private CommandArgumentFabric(ArgumentBuilder<ServerCommandSource, ?> argBuilder) {
        this.argBuilder = argBuilder;
    }

    public static <T> CommandArgumentFabric<T> of(ArgumentBuilder<ServerCommandSource, ?> argBuilder){
        return new CommandArgumentFabric<>(argBuilder);
    }

    public ArgumentBuilder<ServerCommandSource, ?> getArgBuilder() {
        return argBuilder;
    }

    @Override
    public CommandArgumentFabric<T> argument(CommandArgument<?> argument) {
        if (argument instanceof CommandArgumentFabric<?> arg) argBuilder = argBuilder.then(arg.argBuilder);
        else throw new IllegalArgumentException("Argument is not a "+CommandArgumentFabric.class.getSimpleName());
        return this;
    }

    @Override
    public CommandArgumentFabric<T> execute(CommandExecutor executor) {
        argBuilder = argBuilder.executes(CommandFabric.toBrigadierExecutor(executor));
        return this;
    }
}
