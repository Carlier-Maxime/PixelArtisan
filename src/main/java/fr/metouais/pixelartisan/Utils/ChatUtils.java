package fr.metouais.pixelartisan.Utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ChatUtils{
    public static void sendConsoleMessage(String msg){
        for (String line : msg.split("\n")){
            Bukkit.getConsoleSender().sendMessage("[PixelArtisan] "+line);
        }
    }

    public static void sendMessage(CommandSender sender, String msg){
        for (String line : msg.split("\n")){
            sender.sendMessage("§3[PixelArtisan] §r"+line);
        }
    }
}
