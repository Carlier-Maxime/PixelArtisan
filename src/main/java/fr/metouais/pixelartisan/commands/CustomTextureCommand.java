package fr.metouais.pixelartisan.commands;

import dev.jorel.commandapi.CommandAPICommand;
import fr.metouais.pixelartisan.PixelArtisan;
import fr.metouais.pixelartisan.data.DataGenerator;
import fr.metouais.pixelartisan.utils.ChatUtils;
import fr.metouais.pixelartisan.data.DataManager;
import org.bukkit.command.CommandSender;

public class CustomTextureCommand {
    private static CommandAPICommand command;
    private final CommandSender sender;
    private final DataManager dataManager;

    private CustomTextureCommand(CommandSender sender) {
        this.sender=sender;
        dataManager = new DataManager(sender);
    }

    public static CommandAPICommand get() {
        if (command == null) {
            command = new CommandAPICommand("customTexture")
                    .withSubcommand(new CommandAPICommand("generate").executes((sender, args) -> {
                        DataGenerator.generateFromTexturesBlock(sender, PixelArtisan.PATH_CUSTOM_TEXTURE, "custom");
                    }))
                    .withSubcommand(new CommandAPICommand("enable").executes((sender, args) -> {
                        new CustomTextureCommand(sender).enable();
                    }))
                    .withSubcommand(new CommandAPICommand("disable").executes((sender, args) -> {
                        new CustomTextureCommand(sender).disable();
                    }));
        }
        return command;
    }

    private void disable(){
        ChatUtils.sendMessage(sender,"§edisabling custom data..");
        dataManager.loadData(DataManager.DEFAULT_DATA);
        ChatUtils.sendMessage(sender,"§2disable.");
    }

    private void enable(){
        ChatUtils.sendMessage(sender,"§eenabling custom data..");
        dataManager.loadData("custom");
        ChatUtils.sendMessage(sender,"§2enable.");
    }
}
