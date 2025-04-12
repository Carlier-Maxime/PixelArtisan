package fr.metouais.pixelartisan.spigot.command;

import dev.jorel.commandapi.CommandAPICommand;
import fr.metouais.pixelartisan.common.PixelArtisan;
import fr.metouais.pixelartisan.common.util.MessageSender;
import fr.metouais.pixelartisan.spigot.util.MessageSenderSpigot;
import fr.metouais.pixelartisan.spigot.util.Misc;
import org.bukkit.Material;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

public class DebugCommand {
    private static CommandAPICommand command;

    public static CommandAPICommand get() {
        if (command == null) {
            command = new CommandAPICommand("debug")
                    .withSubcommand(new CommandAPICommand("listMaterial")
                            .executes((sender, args) -> {listAllMaterials(MessageSenderSpigot.of(sender));})
                    );
        }
        return command;
    }

    private static void listAllMaterials(MessageSender sender) {
        int count = 0;
        final int LIMIT = 64;
        Path file = PixelArtisan.PATH_DEBUG.resolve("listMaterial.txt");
        try {
            Files.write(file.toFile().toPath(),
                    Arrays.stream(Misc.MATERIALS)
                            .map(Material::name)
                            .collect(Collectors.toList()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Material mat : Misc.MATERIALS) {
            sender.send( "§7- " + mat.name());
            count++;
            if (count >= LIMIT) {
                sender.send( "§e... list truncated to "+LIMIT+" elements, to see the whole list consult the file : "+file);
                break;
            }
        }
    }
}
