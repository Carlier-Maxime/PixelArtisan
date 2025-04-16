package fr.metouais.pixelartisan.common.command;

import fr.metouais.pixelartisan.common.PixelArtisan;
import fr.metouais.pixelartisan.common.command.base.Command;
import fr.metouais.pixelartisan.common.command.base.CommandFactory;

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
                        .execute((sender, args) -> {
                            //TODO
                            sender.send("Generating texture "+args.getArg("name", String.class));
                            /*try {
                                DataGenerator.generateFromTexturesBlock(MessageSenderSpigot.of(sender), PixelArtisan.PATH_INPUT_TEXTURE, (String) args.get(0));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }*/
                        })
                    )
                )
                .subcommand(CommandFactory.builder("use")
                    .argument(argsFactory.fileArgument("name", PixelArtisan.PATH_DATA,
                            List.of(Files::exists, Files::isDirectory, Files::isExecutable, Files::isReadable)
                        ).execute((sender, args) -> {
                            //TODO
                            sender.send("use texture "+args.getArg("name", Path.class));
                            /*String name = ((Path) Objects.requireNonNull(args.get(0))).getFileName().toString();
                            try {
                                new DataManager(sender).loadData(name);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            sender.send("§euse "+name+" texture");*/
                        })
                    )
                );
        }
        return command;
    }
}
