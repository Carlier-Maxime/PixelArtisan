package fr.metouais.pixelartisan.spigot.command.base;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import fr.metouais.pixelartisan.common.command.base.Command;
import fr.metouais.pixelartisan.common.command.base.CommandArgument;
import fr.metouais.pixelartisan.common.command.base.CommandExecutor;
import fr.metouais.pixelartisan.spigot.util.MessageSenderSpigot;

public class CommandSpigot implements Command {
    private CommandAPICommand command;
    private boolean hasArg = false;

    public CommandSpigot(String name) {
        command = new CommandAPICommand(name);
    }

    @Override
    public void register() {
        command.register();
    }

    @Override
    public CommandSpigot aliases(String... aliases) {
        command = command.withAliases(aliases);
        return this;
    }

    @Override
    public CommandSpigot permissionLevel(int level) {
        command = command.withPermission(level>0 ? CommandPermission.OP : CommandPermission.NONE);
        return this;
    }

    @Override
    public CommandSpigot subcommand(Command subcommand) {
        if (subcommand instanceof CommandSpigot cmd) command = command.withSubcommand(cmd.command);
        else throw new IllegalArgumentException("subcommand must be a "+CommandSpigot.class.getSimpleName());
        return this;
    }

    @Override
    public CommandSpigot argument(CommandArgument<?> argument) {
        if (argument instanceof CommandArgumentSpigot<?> argSpigot) {
            if (hasArg) throw new IllegalArgumentException("my implementation of command in spigot not authorise multi-argument on one node");
            else command.withOptionalArguments(argSpigot.getArgument());
        }
        else throw new IllegalArgumentException("argument must be a "+CommandArgumentSpigot.class.getSimpleName());
        hasArg = true;
        return this;
    }

    @Override
    public CommandSpigot execute(CommandExecutor executor) {
        command = command.executes(toCommandAPIExecutor(executor));
        return this;
    }

    public static dev.jorel.commandapi.executors.CommandExecutor toCommandAPIExecutor(CommandExecutor executor) {
        return (sender, args) -> executor.exec(MessageSenderSpigot.of(sender), CommandArgumentsSpigot.of(args));
    }
}
