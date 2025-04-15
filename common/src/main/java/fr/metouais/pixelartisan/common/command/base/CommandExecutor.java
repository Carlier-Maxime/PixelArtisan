package fr.metouais.pixelartisan.common.command.base;

import fr.metouais.pixelartisan.common.util.MessageSender;

public interface CommandExecutor {
    default void exec(MessageSender sender, CommandArgumentsWrapper args) {
        try {
            __exec(sender, args);
        } catch (CommandException e) {
            sender.send("§c"+e.getMessage());
        }
    }
    void __exec(MessageSender sender, CommandArgumentsWrapper args);
}
