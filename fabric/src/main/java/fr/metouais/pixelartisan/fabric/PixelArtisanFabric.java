package fr.metouais.pixelartisan.fabric;

import fr.metouais.pixelartisan.common.PixelArtisan;
import fr.metouais.pixelartisan.common.block.Block;
import fr.metouais.pixelartisan.common.block.BlockPosMaker;
import fr.metouais.pixelartisan.common.command.base.CommandFactory;
import fr.metouais.pixelartisan.common.util.Misc;
import fr.metouais.pixelartisan.common.util.TaskScheduler;
import fr.metouais.pixelartisan.fabric.block.BlockManagerFabric;
import fr.metouais.pixelartisan.fabric.block.BlockPosFabric;
import fr.metouais.pixelartisan.fabric.command.base.CommandArgumentFactoryFabric;
import fr.metouais.pixelartisan.fabric.command.base.CommandFabric;
import fr.metouais.pixelartisan.fabric.util.TaskSchedulerFabric;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.SharedConstants;
import net.minecraft.server.MinecraftServer;

public class PixelArtisanFabric implements ModInitializer {
    private PixelArtisan common;

    @Override
    public void onInitialize() {
        Misc.setGetterMCVersion(() -> SharedConstants.getGameVersion().getId());
        TaskScheduler.set(TaskSchedulerFabric.init());
        BlockPosMaker.setMaker(BlockPosFabric::of);
        CommandFactory.setFactory(CommandFabric::new, new CommandArgumentFactoryFabric());
        Block.setManager(new BlockManagerFabric());
        common = PixelArtisan.getInstance();
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
    }

    private void onServerStopping(MinecraftServer server) {
        common.shutdownNow();
    }
}
