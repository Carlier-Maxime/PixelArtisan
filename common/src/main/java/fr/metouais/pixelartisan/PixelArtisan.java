package fr.metouais.pixelartisan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PixelArtisan {
    public static PixelArtisan instance;
    public static final Logger LOGGER = LoggerFactory.getLogger(PixelArtisan.class);
    private static final Path PATH_PREFIX = Path.of("plugins/PixelArtisan");
    public static final Path PATH_INPUT_TEXTURE = PATH_PREFIX.resolve("input_texture");
    public static final Path PATH_IMAGES = PATH_PREFIX.resolve("images");
    public static final Path PATH_DATA = PATH_PREFIX.resolve("data");
    public static final Path PATH_DEBUG = PATH_PREFIX.resolve("debug");
    public static final Path[] PATHS = {PATH_INPUT_TEXTURE, PATH_IMAGES, PATH_DATA, PATH_DEBUG};
    public static final String GIT_LINK = "https://github.com/Carlier-Maxime/PixelArtisan";
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private PixelArtisan() {}

    public static PixelArtisan getInstance() {
        if (instance == null) instance = new PixelArtisan();
        return instance;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }
}