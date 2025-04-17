package fr.metouais.pixelartisan.common.command.base;

import fr.metouais.pixelartisan.common.util.MessageSender;

public class CommandContext {
    private final MessageSender sender;
    private final CommandArgumentsWrapper arguments;
    private CommandContext(MessageSender sender, CommandArguments arguments) {
        this.sender = sender;
        this.arguments = arguments.wrapper();
    }
    public static CommandContext of(MessageSender sender, CommandArguments arguments) {
        return new CommandContext(sender, arguments);
    }
    public MessageSender getSender() {
        return sender;
    }
    public CommandArgumentsWrapper getArguments() {
        return arguments;
    }
}
