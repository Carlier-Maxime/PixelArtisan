package fr.metouais.pixelartisan.fabric.command.base;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fr.metouais.pixelartisan.common.command.base.Command;
import fr.metouais.pixelartisan.common.command.base.CommandExecutor;
import fr.metouais.pixelartisan.fabric.util.MessageSenderFabric;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class CommandFabric implements Command {
    private LiteralArgumentBuilder<ServerCommandSource> command;

    public CommandFabric(String name) {
        command = CommandManager.literal(name);
    }

    @Override
    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(command));
    }

    @Override
    public Command aliases(String... aliases) {
        return null;
    }

    @Override
    public Command description(String description) {
        return null;
    }

    @Override
    public Command permissionLevel(int level) {
        return null;
    }

    @Override
    public Command subcommand(Command subcommand) {
        if (subcommand instanceof CommandFabric cmd) command = command.then(cmd.command);
        else throw new IllegalArgumentException("subcommand must be a command fabric");
        return this;
    }

    @Override
    public Command execute(CommandExecutor executor) {
        command = command.executes(ctx -> {
            executor.exec(MessageSenderFabric.of(ctx.getSource()), null);
            return 1;
        });
        return this;
    }
}
