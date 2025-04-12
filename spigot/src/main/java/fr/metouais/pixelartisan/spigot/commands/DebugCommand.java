package fr.metouais.pixelartisan.spigot.commands;

import dev.jorel.commandapi.CommandAPICommand;
import fr.metouais.pixelartisan.common.PixelArtisan;
import fr.metouais.pixelartisan.spigot.utils.ChatUtils;
import fr.metouais.pixelartisan.spigot.utils.Misc;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

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
                            .executes((sender, args) -> {listAllMaterials(sender);})
                    );
        }
        return command;
    }

    private static void listAllMaterials(CommandSender sender) {
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
            ChatUtils.sendMessage(sender, "§7- " + mat.name());
            count++;
            if (count >= LIMIT) {
                ChatUtils.sendMessage(sender, "§e... list truncated to "+LIMIT+" elements, to see the whole list consult the file : "+file);
                break;
            }
        }
    }
}
