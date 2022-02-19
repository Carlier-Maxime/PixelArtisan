package fr.metouais.pixelartisan.Utils;

import org.bukkit.Bukkit;

public class ChatUtils{
    public static void sendConsoleMessage(String msg){
        for (String line : msg.split("\n")){
            Bukkit.getConsoleSender().sendMessage("[PixelArtisan] "+line);
        }
    }
}
