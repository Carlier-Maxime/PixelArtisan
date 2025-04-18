package fr.metouais.pixelartisan.spigot;

import dev.jorel.commandapi.CommandAPI;
import fr.metouais.pixelartisan.common.block.Block;
import fr.metouais.pixelartisan.common.block.BlockPosMaker;
import fr.metouais.pixelartisan.common.command.base.CommandFactory;
import fr.metouais.pixelartisan.common.util.MessageSender;
import fr.metouais.pixelartisan.common.PixelArtisan;
import fr.metouais.pixelartisan.common.util.Misc;
import fr.metouais.pixelartisan.common.util.TaskScheduler;
import fr.metouais.pixelartisan.spigot.block.BlockManagerSpigot;
import fr.metouais.pixelartisan.spigot.block.BlockPosSpigot;
import fr.metouais.pixelartisan.spigot.command.base.CommandArgumentFactorySpigot;
import fr.metouais.pixelartisan.spigot.command.base.CommandSpigot;
import fr.metouais.pixelartisan.spigot.util.TaskSchedulerSpigot;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PixelArtisanSpigot extends JavaPlugin {
    private static PixelArtisanSpigot instance;
    private PixelArtisan common;

    @Override
    public void onEnable() {
        super.onEnable();
        CommandAPI.onEnable();
        instance = this;
        Misc.setGetterMCVersion(() -> {
            String version = Bukkit.getVersion();
            return version.substring(version.indexOf("(MC: ")+5, version.indexOf(")"));
        });
        TaskScheduler.set(new TaskSchedulerSpigot());
        BlockPosMaker.setMaker(BlockPosSpigot::of);
        CommandFactory.setFactory(CommandSpigot::new, new CommandArgumentFactorySpigot());
        Block.setManager(new BlockManagerSpigot());
        common = PixelArtisan.getInstance();
        common.start();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        common.shutdownNow();
        CommandAPI.onDisable();
        MessageSender.CONSOLE.send("PixelArtisan disable");
    }

    public static PixelArtisanSpigot getInstance() {
        return instance;
    }
}
