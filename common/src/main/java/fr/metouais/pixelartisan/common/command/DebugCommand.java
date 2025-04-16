package fr.metouais.pixelartisan.common.command;

import fr.metouais.pixelartisan.common.command.base.Command;
import fr.metouais.pixelartisan.common.command.base.CommandFactory;
import fr.metouais.pixelartisan.common.util.MessageSender;

public class DebugCommand {
    private static Command command;

    public static Command get() {
        if (command == null) {
            command = CommandFactory.builder("debug")
                .subcommand(CommandFactory.builder("listMaterial")
                    .execute((sender, args) -> listAllMaterials(sender))
                );
        }
        return command;
    }

    private static void listAllMaterials(MessageSender sender) {
        //TODO
        sender.send("list all materials");
        /*int count = 0;
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
        }*/
    }
}
