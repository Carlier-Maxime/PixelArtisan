package fr.metouais.pixelartisan.fabric.command.base;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.metouais.pixelartisan.common.command.base.*;
import fr.metouais.pixelartisan.fabric.util.MessageSenderFabric;
import fr.metouais.pixelartisan.fabric.util.WorldFabric;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class CommandFabric implements Command {
    private LiteralArgumentBuilder<ServerCommandSource> command;
    private String[] aliases = new String[0];

    public CommandFabric(String name) {
        command = CommandManager.literal(name);
    }

    @Override
    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            var cmd = dispatcher.register(command);
            for (String alias : aliases) dispatcher.getRoot().addChild(buildRedirect(alias, cmd));
        });
    }


    /* the original version of this code can be found on https://github.com/PaperMC/Velocity/blob/8abc9c80a69158ebae0121fda78b55c865c0abad/proxy/src/main/java/com/velocitypowered/proxy/util/BrigadierUtils.java#L38*/
    public static LiteralCommandNode<ServerCommandSource> buildRedirect(final String alias, final LiteralCommandNode<ServerCommandSource> destination) {
        var builder = CommandManager
                .literal(alias)
                .requires(destination.getRequirement())
                .forward(destination.getRedirect(), destination.getRedirectModifier(), destination.isFork())
                .executes(destination.getCommand());
        for (CommandNode<ServerCommandSource> child : destination.getChildren()) builder.then(child);
        return builder.build();
    }

    @Override
    public CommandFabric aliases(String... aliases) {
        this.aliases = aliases;
        return this;
    }

    @Override
    public CommandFabric permissionLevel(int level) {
        command = command.requires((source) -> source.hasPermissionLevel(level));
        return this;
    }

    @Override
    public CommandFabric subcommand(Command subcommand) {
        if (subcommand instanceof CommandFabric cmdb) {
            var cmd = cmdb.command.build();
            for (String alias : cmdb.aliases) command.then(buildRedirect(alias, cmd));
            command = command.then(cmd);
        }
        else throw new IllegalArgumentException("subcommand must be a "+CommandFabric.class.getSimpleName());
        return this;
    }

    @Override
    public CommandFabric argument(CommandArgument<?> argument) {
        if (argument instanceof CommandArgumentFabric<?> argFabric) return argument(argFabric);
        else if (argument instanceof CommandCustomArgument<?,?> argCustom) return argument(argCustom.getBase());
        throw new IllegalArgumentException("argument must be a "+CommandArgument.class.getName());
    }

    public CommandFabric argument(CommandArgumentFabric<?> argFabric) {
        argFabric.build();
        command = command.then(argFabric.getArgBuilder());
        return this;
    }

    @Override
    public CommandFabric execute(CommandExecutor executor) {
        command = command.executes(toBrigadierExecutor(executor));
        return this;
    }

    public static com.mojang.brigadier.Command<ServerCommandSource> toBrigadierExecutor(CommandExecutor executor) {
        return ctx -> {
            executor.exec(CommandContext.of(MessageSenderFabric.of(ctx.getSource()), CommandArgumentsFabric.of(ctx), WorldFabric.of(ctx.getSource().getWorld())));
            return 1;
        };
    }
}
