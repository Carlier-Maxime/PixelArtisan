package fr.metouais.pixelartisan.fabric.util;

import fr.metouais.pixelartisan.common.util.MessageSender;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class MessageSenderFabric implements MessageSender {
    private final ServerCommandSource source;

    private MessageSenderFabric(ServerCommandSource source) {
        this.source = source;
    }

    public static MessageSenderFabric of(ServerCommandSource source) {
        return new MessageSenderFabric(source);
    }

    @Override
    public void send(String msg) {
        source.sendFeedback(() -> Text.literal(PREFIX_WITH_COLOR+msg), false);
    }
}
