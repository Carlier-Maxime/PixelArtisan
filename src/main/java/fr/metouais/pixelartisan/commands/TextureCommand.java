package fr.metouais.pixelartisan.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import fr.metouais.pixelartisan.PixelArtisan;
import fr.metouais.pixelartisan.data.DataGenerator;
import fr.metouais.pixelartisan.utils.ChatUtils;
import fr.metouais.pixelartisan.data.DataManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class TextureCommand {
    private static CommandAPICommand command;

    private TextureCommand() {}

    public static CommandAPICommand get() {
        if (command == null) {
            command = new CommandAPICommand("texture")
                    .withSubcommand(new CommandAPICommand("generate")
                            .withArguments(new StringArgument("name"))
                            .executes((sender, args) -> {
                                try {
                                    DataGenerator.generateFromTexturesBlock(sender, PixelArtisan.PATH_INPUT_TEXTURE, (String) args.get(0));
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }))
                    .withSubcommand(new CommandAPICommand("use")
                            .withArguments(Arguments.FileArgument("name", PixelArtisan.PATH_DATA,
                                    Files::exists, Files::isDirectory, Files::isExecutable, Files::isReadable))
                            .executes((sender, args) -> {
                                String name = ((Path) Objects.requireNonNull(args.get(0))).getFileName().toString();
                                try {
                                    new DataManager(sender).loadData(name);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                ChatUtils.sendMessage(sender,"§euse "+name+" texture");
                            })
                    );
        }
        return command;
    }
}
