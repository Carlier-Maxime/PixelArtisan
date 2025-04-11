package fr.metouais.pixelartisan;

import dev.jorel.commandapi.CommandAPI;
import fr.metouais.pixelartisan.commands.PixelArtisanCommand;
import fr.metouais.pixelartisan.utils.ChatUtils;
import fr.metouais.pixelartisan.utils.Misc;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PixelArtisanSpigot extends JavaPlugin {
    private static PixelArtisanSpigot instance;
    private static final PixelArtisan common = PixelArtisan.getInstance();

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        try {
            for (Path path : PixelArtisan.PATHS) Files.createDirectories(path);
        } catch (IOException e) {
            PixelArtisan.LOGGER.error("Failed creation folder of PixelArtisan", e);
        }

        CommandAPI.onEnable();
        PixelArtisanCommand.get().register();

        ChatUtils.sendConsoleMessage("NB MATERIAL = "+ Misc.MATERIALS.length);
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
        common.getExecutorService().shutdownNow();
        CommandAPI.onDisable();
        ChatUtils.sendConsoleMessage("PixelArtisan disable");
    }

    public static PixelArtisanSpigot getInstance() {
        return instance;
    }
}
