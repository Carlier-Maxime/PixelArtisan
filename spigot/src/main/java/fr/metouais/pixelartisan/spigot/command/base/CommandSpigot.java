package fr.metouais.pixelartisan.spigot.command.base;

import com.google.common.collect.Lists;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import fr.metouais.pixelartisan.common.command.base.Command;
import fr.metouais.pixelartisan.common.command.base.CommandArgument;
import fr.metouais.pixelartisan.common.command.base.CommandExecutor;
import fr.metouais.pixelartisan.spigot.util.MessageSenderSpigot;

import java.util.List;

public class CommandSpigot implements Command {
    private CommandAPICommand command;
    private boolean hasArg = false;
    private final List<CommandExecutor> executors = Lists.newArrayList();

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
            int i=1;
            var child = argSpigot;
            while (child != null) {
                while (executors.size() <= i) executors.add(null);
                executors.set(i++, child.getExecutor());
                command.withOptionalArguments(child.getArgument());
                child = child.getChild();
            }
        }
        else throw new IllegalArgumentException("argument must be a "+CommandArgumentSpigot.class.getSimpleName());
        hasArg = true;
        return this;
    }

    @Override
    public CommandSpigot execute(CommandExecutor executor) {
        if (executors.isEmpty()) executors.add(executor);
        else executors.set(0, executor);
        command = command.executes((cmdSender, cmdArgs) -> {
            var sender = MessageSenderSpigot.of(cmdSender);
            var args = CommandArgumentsSpigot.of(cmdArgs);
            var execFunc = executors.get(cmdArgs.args().length);
            if (execFunc == null) throw new IllegalArgumentException("no such executor of this number of args");
            execFunc.exec(sender, args);
        });
        return this;
    }
}
