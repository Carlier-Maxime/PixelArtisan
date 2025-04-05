package fr.metouais.pixelartisan;

import dev.jorel.commandapi.CommandAPI;
import fr.metouais.pixelartisan.commands.PixelArtisanCommand;
import fr.metouais.pixelartisan.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PixelArtisan extends JavaPlugin {
    private static PixelArtisan instance;
    public static final Logger LOGGER = LoggerFactory.getLogger(PixelArtisan.class);
    public static final Path PATH_INPUT_TEXTURE = Path.of("plugins/PixelArtisan/input_texture");
    public static final Path PATH_IMAGES = Path.of("plugins/PixelArtisan/images");
    public static final Path PATH_DATA = Path.of("plugins/PixelArtisan/data");
    public static final Path PATH_DEBUG = Path.of("plugins/PixelArtisan/debug");
    private static final Path[] PATHS = {PATH_INPUT_TEXTURE, PATH_IMAGES, PATH_DATA, PATH_DEBUG};
    public static final String GIT_LINK = "https://github.com/Carlier-Maxime/PixelArtisan";
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        try {
            for (Path path : PATHS) Files.createDirectories(path);
        } catch (IOException e) {
            LOGGER.error("Failed creation folder of PixelArtisan", e);
        }

        CommandAPI.onEnable();
        PixelArtisanCommand.get().register();

        ChatUtils.sendConsoleMessage("NB MATERIAL = "+ Material.values().length);
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
        CommandAPI.onDisable();
        ChatUtils.sendConsoleMessage("PixelArtisan disable");
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public static PixelArtisan getInstance() {
        return instance;
    }
}
