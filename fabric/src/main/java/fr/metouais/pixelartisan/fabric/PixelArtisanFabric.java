package fr.metouais.pixelartisan.fabric;

import fr.metouais.pixelartisan.common.PixelArtisan;
import fr.metouais.pixelartisan.common.command.base.CommandFactory;
import fr.metouais.pixelartisan.fabric.command.base.CommandArgumentFactoryFabric;
import fr.metouais.pixelartisan.fabric.command.base.CommandFabric;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class PixelArtisanFabric implements ModInitializer {
    private PixelArtisan common;

    @Override
    public void onInitialize() {
        CommandFactory.setFactory(CommandFabric::new, new CommandArgumentFactoryFabric());
        common = PixelArtisan.getInstance();
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStopping);
    }

    private void onServerStopping(MinecraftServer server) {
        common.shutdownNow();
    }
}
