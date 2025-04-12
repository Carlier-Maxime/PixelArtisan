package fr.metouais.pixelartisan.common.commands.base;

import fr.metouais.pixelartisan.common.utils.MessageSender;

import java.util.Map;

public interface CommandExecutor {
    void exec(MessageSender sender, Map<String, Object> args);
}
