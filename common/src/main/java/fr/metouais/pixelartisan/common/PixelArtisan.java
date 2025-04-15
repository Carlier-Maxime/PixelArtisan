package fr.metouais.pixelartisan.common;

import fr.metouais.pixelartisan.common.command.base.CommandFactory;
import fr.metouais.pixelartisan.common.util.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PixelArtisan {
    public static PixelArtisan instance;
    public static final Logger LOGGER = LoggerFactory.getLogger(PixelArtisan.class+"prefix");
    private static final Path PATH_PREFIX = Path.of("data/PixelArtisan");
    public static final Path PATH_INPUT_TEXTURE = PATH_PREFIX.resolve("input_texture");
    public static final Path PATH_IMAGES = PATH_PREFIX.resolve("images");
    public static final Path PATH_DATA = PATH_PREFIX.resolve("data");
    public static final Path PATH_DEBUG = PATH_PREFIX.resolve("debug");
    public static final Path[] PATHS = {PATH_INPUT_TEXTURE, PATH_IMAGES, PATH_DATA, PATH_DEBUG};
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private PixelArtisan() {
        try {
            for (Path path : PATHS) Files.createDirectories(path);
        } catch (IOException e) {
            LOGGER.error("Failed creation folder of PixelArtisan", e);
        }
        LOGGER.info(Info.NAME+" enable\n"+
            "\t\t\t\t - Create by " + Info.AUTHOR + '\n' +
            "\t\t\t\t - Version : " + Info.VERSION + '\n' +
            "\t\t\t\t - Description : " + Info.DESCRIPTION
        );
        var argsFactory = CommandFactory.argsFactory();
        CommandFactory.builder("pa").aliases(Info.ID).permissionLevel(2)
        .subcommand(CommandFactory.builder("create")
            .aliases("build", "make")
            .execute((sender, args) -> sender.send("a base of command create"))
            .argument(argsFactory.fileArgument("filename", PATH_IMAGES, List.of(Files::exists, Files::isRegularFile, Files::isReadable))
                .argument(argsFactory.integerArgument("size", 1)
                    .execute((sender, args) -> {
                        sender.send(args.getArg("filename", Path.class)+" and size "+args.getArg("size", Integer.class));
                    })
                )
                .execute((sender, args) -> {
                    sender.send("a filename is : "+args.getArg("filename", Path.class));
                })
            )
        ).subcommand(CommandFactory.builder("texture")
            .execute((sender, args) -> sender.send("a base of command texture"))
        ).subcommand(CommandFactory.builder("debug")
            .argument(
                argsFactory.integerArgument("count", 2, 10).suggests(List.of(2, 4, 8))
                .argument(argsFactory.wordArgument("name").suggests(List.of("toto", "titi", "prout"))
                    .execute((sender, args) -> sender.send("a count is : "+args.getArg("count", Integer.class)+" and name is : "+args.getArg("name", String.class)))
                )
                .execute((sender, args) -> sender.send("a count is : "+args.getArg("count", Integer.class)))
            )
            .execute((sender, args) -> sender.send("a base of command debug"))
        ).register();
    }

    public static PixelArtisan getInstance() {
        if (instance == null) instance = new PixelArtisan();
        return instance;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void shutdownNow(){
        executorService.shutdownNow();
    }
}