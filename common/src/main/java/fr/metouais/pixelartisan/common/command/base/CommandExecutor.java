package fr.metouais.pixelartisan.common.command.base;

import fr.metouais.pixelartisan.common.util.MessageSender;

public interface CommandExecutor {
    void exec(MessageSender sender, CommandArgumentsWrapper args);
}
