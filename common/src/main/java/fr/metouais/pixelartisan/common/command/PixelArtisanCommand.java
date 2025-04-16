package fr.metouais.pixelartisan.common.command;

import fr.metouais.pixelartisan.common.command.base.Command;
import fr.metouais.pixelartisan.common.command.base.CommandFactory;
import fr.metouais.pixelartisan.common.util.Info;

public class PixelArtisanCommand {
    private static Command command;

    private PixelArtisanCommand() {}

    public static Command get() {
        if (command == null) {
            command = CommandFactory.builder("pa").aliases(Info.ID)
                .permissionLevel(2)
                .subcommand(CreateCommand.get())
                .subcommand(TextureCommand.get())
                .subcommand(DebugCommand.get());
        }
        return command;
    }
}
