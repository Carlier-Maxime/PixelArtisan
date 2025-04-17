package fr.metouais.pixelartisan.spigot.command.base;

import com.google.common.collect.Lists;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import fr.metouais.pixelartisan.common.command.base.*;
import fr.metouais.pixelartisan.spigot.util.MessageSenderSpigot;
import fr.metouais.pixelartisan.spigot.util.WorldSpigot;

import java.util.List;

public class CommandSpigot implements Command {
    private CommandAPICommand command;
    private boolean hasArg = false;
    private final List<CommandExecutor> executors = Lists.newArrayList();

    public CommandSpigot(String name) {
        CommandExecutor executor = (ctx) -> {
            var nbArgs = ctx.getArguments().size();
            if (nbArgs >= executors.size())  throw new CommandException("Incomplete or invalid command");
            var execFunc = executors.get((int) nbArgs);
            if (execFunc == null) throw new CommandException("no such executor of this number of args");
            execFunc.exec(ctx);
        } ;
        command = new CommandAPICommand(name).executesNative((sender, args) -> {
            executor.exec(CommandContext.of(MessageSenderSpigot.of(sender), CommandArgumentsSpigot.of(args), WorldSpigot.of(sender.getWorld())));
        });
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
        if (hasArg) throw new IllegalArgumentException("my implementation of command in spigot not authorise multi-argument on one node");
        int i=1;
        var child = argument;
        while (child != null) {
            while (executors.size() <= i) executors.add(null);
            executors.set(i++, child.getExecutor());
            argumentStep(child);
            child = child.getChild();
        }
        hasArg = true;
        return this;
    }

    private void argumentStep(CommandArgument<?> argument) {
        if (argument instanceof CommandArgumentSpigot<?> argSpigot) argumentStep(argSpigot);
        else if (argument instanceof CommandCustomArgument<?,?> argCustom) argumentStep(argCustom);
        else throw new IllegalArgumentException("argument must be a "+CommandArgumentSpigot.class.getSimpleName());
    }

    private void argumentStep(CommandArgumentSpigot<?> argSpigot) {
        command.withOptionalArguments(argSpigot.getArgument());
    }

    private void argumentStep(CommandCustomArgument<?,?> argCustom) {
        argumentStep(argCustom.getBase());
    }

    @Override
    public CommandSpigot execute(CommandExecutor executor) {
        if (executors.isEmpty()) executors.add(executor);
        else executors.set(0, executor);
        return this;
    }
}
