package fr.metouais.pixelartisan;

import fr.metouais.pixelartisan.Utils.ChatUtils;
import fr.metouais.pixelartisan.commands.PixelArtisanCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class PixelArtisan extends JavaPlugin {
    @Override
    public void onEnable() {
        super.onEnable();

        Objects.requireNonNull(getCommand("pa")).setExecutor(new PixelArtisanCommand());

        ChatUtils.sendConsoleMessage(
                getDescription().getName()+" enable\n"+
                "  - Plugin create by " + getDescription().getAuthors() + '\n' +
                "  - Version : " + getDescription().getVersion() + '\n' +
                "  - Description : " + getDescription().getDescription()
        );
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Bukkit.getConsoleSender().sendMessage("PixelArtisan disable");
    }
}
