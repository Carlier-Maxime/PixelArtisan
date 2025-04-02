package fr.metouais.pixelartisan;

import fr.metouais.pixelartisan.Utils.ChatUtils;
import fr.metouais.pixelartisan.commands.PixelArtisanCommand;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PixelArtisan extends JavaPlugin {
    private static PixelArtisan instance;
    public static final Logger LOGGER = LoggerFactory.getLogger(PixelArtisan.class);
    public static final Path PATH_CUSTOM_TEXTURE = Path.of("./plugins/PixelArtisan/custom_texture");
    public static final Path PATH_IMAGES = Path.of("./plugins/PixelArtisan/images");
    public static final String GIT_LINK = "https://github.com/Carlier-Maxime/PixelArtisan";
    private final ExecutorService executorService = Executors.newCachedThreadPool();

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
            Files.createDirectories(PATH_CUSTOM_TEXTURE);
            Files.createDirectories(Path.of(s+"/images"));
            Files.createDirectories(Path.of(s+"/data"));
        } catch (IOException e) {
            LOGGER.error("Failed creation folder of PixelArtisan", e);
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
        executorService.shutdownNow();
        ChatUtils.sendConsoleMessage("PixelArtisan disable");
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public static PixelArtisan getInstance() {
        return instance;
    }
}
