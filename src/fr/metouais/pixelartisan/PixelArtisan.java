package fr.metouais.pixelartisan;

import fr.metouais.pixelartisan.Utils.ChatUtils;
import fr.metouais.pixelartisan.commands.PixelArtisanCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class PixelArtisan extends JavaPlugin {
    @Override
    public void onEnable() {
        super.onEnable();

        PluginCommand pa = Objects.requireNonNull(getCommand("pa"));
        PixelArtisanCommand paCmd = new PixelArtisanCommand();
        pa.setExecutor(paCmd);
        pa.setTabCompleter(paCmd);

        try {
            String s = "./plugins/PixelArtisan";
            Files.createDirectories(Path.of(s));
            Files.createDirectories(Path.of(s+"/custom_texture"));
            Files.createDirectories(Path.of(s+"/images"));
        } catch (IOException e) {
            e.printStackTrace();
        }

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
