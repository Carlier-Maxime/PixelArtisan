package fr.metouais.pixelartisan.common.command;

import fr.metouais.pixelartisan.common.PixelArtisan;
import fr.metouais.pixelartisan.common.command.base.Command;
import fr.metouais.pixelartisan.common.command.base.CommandException;
import fr.metouais.pixelartisan.common.command.base.CommandFactory;
import fr.metouais.pixelartisan.common.data.DataGenerator;
import fr.metouais.pixelartisan.common.data.DataManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TextureCommand {
    private static Command command;

    private TextureCommand() {}

    public static Command get() {
        if (command == null) {
            var argsFactory = CommandFactory.argsFactory();
            command = CommandFactory.builder("texture")
                .subcommand(CommandFactory.builder("generate")
                    .argument(argsFactory.wordArgument("name")
                        .execute(ctx -> {
                            try {
                                DataGenerator.generateFromTexturesBlock(ctx.getSender(), PixelArtisan.PATH_INPUT_TEXTURE, ctx.getArguments().getArg("name", String.class));
                            } catch (IOException e) {
                                throw new CommandException(e);
                            }
                        })
                    )
                )
                .subcommand(CommandFactory.builder("use")
                    .argument(argsFactory.fileArgument("name", PixelArtisan.PATH_DATA,
                            List.of(Files::exists, Files::isDirectory, Files::isExecutable, Files::isReadable)
                        ).execute(ctx -> {
                            String name = ctx.getArguments().getArg("name", Path.class).getFileName().toString();
                            try {
                                new DataManager(ctx.getSender()).loadData(name);
                            } catch (IOException e) {
                                throw new CommandException(e);
                            }
                            ctx.getSender().send("§euse "+name+" texture");
                        })
                    )
                );
        }
        return command;
    }
}
