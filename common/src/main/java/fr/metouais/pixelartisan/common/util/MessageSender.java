package fr.metouais.pixelartisan.common.util;

import fr.metouais.pixelartisan.common.PixelArtisan;

public interface MessageSender {
    String PREFIX = "["+Info.NAME+"] ";
    String PREFIX_WITH_COLOR = "§3["+Info.NAME+"]§r ";
    MessageSender CONSOLE = msg -> PixelArtisan.LOGGER.info("{}{}", PREFIX, msg);
    void send(String msg);
}
