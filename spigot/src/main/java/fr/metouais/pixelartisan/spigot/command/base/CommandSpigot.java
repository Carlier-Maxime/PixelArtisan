package fr.metouais.pixelartisan.spigot.command.base;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import fr.metouais.pixelartisan.common.command.base.Command;
import fr.metouais.pixelartisan.common.command.base.CommandExecutor;
import fr.metouais.pixelartisan.spigot.util.MessageSenderSpigot;

public class CommandSpigot implements Command {
    private CommandAPICommand command;

    public CommandSpigot(String name) {
        command = new CommandAPICommand(name);
    }

    @Override
    public void register() {
        command.register();
    }

    @Override
    public Command aliases(String... aliases) {
        command = command.withAliases(aliases);
        return this;
    }

    @Override
    public Command permissionLevel(int level) {
        command = command.withPermission(level>0 ? CommandPermission.OP : CommandPermission.NONE);
        return this;
    }

    @Override
    public Command subcommand(Command subcommand) {
        if (subcommand instanceof CommandSpigot cmd) command = command.withSubcommand(cmd.command);
        else throw new IllegalArgumentException("subcommand must be a CommandSpigot");
        return this;
    }

    @Override
    public Command execute(CommandExecutor executor) {
        command = command.executes((sender, args) -> {
            executor.exec(MessageSenderSpigot.of(sender), CommandArgumentsSpigot.of(args));
        });
        return this;
    }
}
