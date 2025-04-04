package fr.metouais.pixelartisan.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;

public class PixelArtisanCommand {
    private static CommandAPICommand command;

    private PixelArtisanCommand() {}

    public static CommandAPICommand get() {
        if (command == null) {
            command = new CommandAPICommand("pa").withAliases("PixelArtisan", "PixelArtisan")
                    .withShortDescription("PixelArtisan plugin command prefix")
                    .withPermission(CommandPermission.OP)
                    .withSubcommand(CreateCommand.get())
                    .withSubcommand(TextureCommand.get());
        }
        return command;
    }
}
