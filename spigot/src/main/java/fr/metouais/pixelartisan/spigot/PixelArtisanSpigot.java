package fr.metouais.pixelartisan.spigot;

import dev.jorel.commandapi.CommandAPI;
import fr.metouais.pixelartisan.common.util.MessageSender;
import fr.metouais.pixelartisan.spigot.command.PixelArtisanCommand;
import fr.metouais.pixelartisan.common.PixelArtisan;
import fr.metouais.pixelartisan.spigot.util.Misc;
import org.bukkit.plugin.java.JavaPlugin;

public class PixelArtisanSpigot extends JavaPlugin {
    private static PixelArtisanSpigot instance;
    private PixelArtisan common;

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        common = PixelArtisan.getInstance();

        CommandAPI.onEnable();
        PixelArtisanCommand.get().register();

        MessageSender.CONSOLE.send("NB MATERIAL = "+ Misc.MATERIALS.length);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        common.shutdownNow();
        CommandAPI.onDisable();
        MessageSender.CONSOLE.send("PixelArtisan disable");
    }

    public static PixelArtisanSpigot getInstance() {
        return instance;
    }
}
