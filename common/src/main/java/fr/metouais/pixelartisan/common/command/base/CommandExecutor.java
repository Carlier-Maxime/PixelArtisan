package fr.metouais.pixelartisan.common.command.base;

import fr.metouais.pixelartisan.common.util.MessageSender;

import java.util.Map;

public interface CommandExecutor {
    void exec(MessageSender sender, Map<String, Object> args);
}
