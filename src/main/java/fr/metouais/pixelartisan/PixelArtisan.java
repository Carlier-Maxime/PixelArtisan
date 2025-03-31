package fr.metouais.pixelartisan;

import fr.metouais.pixelartisan.Utils.ChatUtils;
import fr.metouais.pixelartisan.commands.PixelArtisanCommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class PixelArtisan extends JavaPlugin {
    private static PixelArtisan instance;

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        PluginCommand pa = Objects.requireNonNull(getCommand("pa"));
        PixelArtisanCommand paCmd = new PixelArtisanCommand();
        pa.setExecutor(paCmd);
        pa.setTabCompleter(paCmd);

        ChatUtils.sendConsoleMessage("NB MATERIAL = "+ Material.values().length);

        try {
            String s = "./plugins/PixelArtisan";
            Files.createDirectories(Path.of(s));
            Files.createDirectories(Path.of(s+"/custom_texture"));
            Files.createDirectories(Path.of(s+"/images"));
            Files.createDirectories(Path.of(s+"/data"));
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
        ChatUtils.sendConsoleMessage("PixelArtisan disable");
    }

    public static PixelArtisan getInstance() {
        return instance;
    }
}
