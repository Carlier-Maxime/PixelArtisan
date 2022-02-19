package fr.metouais.pixelartisan;

import fr.metouais.pixelartisan.Utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PixelArtisan extends JavaPlugin {
    @Override
    public void onEnable() {
        super.onEnable();
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
