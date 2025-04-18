package fr.metouais.pixelartisan.common.command;

import fr.metouais.pixelartisan.common.PixelArtisan;
import fr.metouais.pixelartisan.common.block.Block;
import fr.metouais.pixelartisan.common.block.IBlock;
import fr.metouais.pixelartisan.common.command.base.Command;
import fr.metouais.pixelartisan.common.command.base.CommandFactory;
import fr.metouais.pixelartisan.common.util.MessageSender;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class DebugCommand {
    private static Command command;

    public static Command get() {
        if (command == null) {
            command = CommandFactory.builder("debug")
                .subcommand(CommandFactory.builder("listBlocks")
                    .execute(ctx -> listAllBlocks(ctx.getSender()))
                )
                .subcommand(CommandFactory.builder("properties")
                    .argument(CommandFactory.argsFactory().wordArgument("blockId")
                        .execute(ctx -> {
                            var b = Block.of(ctx.getArguments().getArg("blockId", String.class));
                            ctx.getSender().send(b.getId()+"{hasGravity: "+b.hasGravity()+"; isFullBlock: "+b.isFullBlock()+"}");
                        })
                    )
                );
        }
        return command;
    }

    private static void listAllBlocks(MessageSender sender) {
        int count = 0;
        final int LIMIT = 64;
        Path file = PixelArtisan.PATH_DEBUG.resolve("listBlock.txt");
        try {
            Files.write(file.toFile().toPath(), Block.all().stream().map(IBlock::getId).collect(Collectors.toList()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (var block : Block.all()) {
            sender.send( "§7- " + block.getId());
            count++;
            if (count >= LIMIT) {
                sender.send( "§e... list truncated to "+LIMIT+" elements, to see the whole list consult the file : "+file);
                break;
            }
        }
    }
}
