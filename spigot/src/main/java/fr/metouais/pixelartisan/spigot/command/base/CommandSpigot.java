package fr.metouais.pixelartisan.spigot.command.base;

import dev.jorel.commandapi.CommandAPICommand;
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
        return null;
    }

    @Override
    public Command execute(CommandExecutor executor) {
        command.executes((sender, args) -> {
            executor.exec(MessageSenderSpigot.of(sender), args.argsMap());
        });
        return this;
    }
}
