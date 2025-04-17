package fr.metouais.pixelartisan.common.command.base;

import fr.metouais.pixelartisan.common.util.MessageSender;
import fr.metouais.pixelartisan.common.util.World;

public class CommandContext {
    private final MessageSender sender;
    private final CommandArgumentsWrapper arguments;
    private final World world;
    private CommandContext(MessageSender sender, CommandArguments arguments, World world) {
        if (arguments instanceof CommandArgumentsWrapper) {
            var nameWrapper = CommandArgumentsWrapper.class.getSimpleName();
            var nameCtx = CommandContext.class.getSimpleName();
            throw new IllegalArgumentException("not create "+nameCtx+" with "+nameWrapper+" because wrapper is automatically created");
        }
        this.sender = sender;
        this.arguments = arguments.wrapper(this);
        this.world = world;
    }
    public static CommandContext of(MessageSender sender, CommandArguments arguments, World world) {
        return new CommandContext(sender, arguments, world);
    }
    public MessageSender getSender() {
        return sender;
    }
    public CommandArgumentsWrapper getArguments() {
        return arguments;
    }
    public World getWorld() {
        return world;
    }
}
