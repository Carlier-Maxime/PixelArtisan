package fr.metouais.pixelartisan;

import dev.jorel.commandapi.CommandAPI;
import fr.metouais.pixelartisan.commands.PixelArtisanCommand;
import fr.metouais.pixelartisan.utils.ChatUtils;
import fr.metouais.pixelartisan.utils.Misc;
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

        ChatUtils.sendConsoleMessage("NB MATERIAL = "+ Misc.MATERIALS.length);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        common.shutdownNow();
        CommandAPI.onDisable();
        ChatUtils.sendConsoleMessage("PixelArtisan disable");
    }

    public static PixelArtisanSpigot getInstance() {
        return instance;
    }
}
