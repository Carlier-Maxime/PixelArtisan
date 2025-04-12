package fr.metouais.pixelartisan.spigot.util;

import fr.metouais.pixelartisan.common.util.MessageSender;
import org.bukkit.command.CommandSender;

public class MessageSenderSpigot implements MessageSender {
    private final CommandSender sender;

    private MessageSenderSpigot(CommandSender sender) {
        this.sender = sender;
    }

    public static MessageSenderSpigot of(CommandSender sender) {
        return new MessageSenderSpigot(sender);
    }

    @Override
    public void send(String msg) {
        sender.sendMessage(PREFIX_WITH_COLOR+msg);
    }
}
