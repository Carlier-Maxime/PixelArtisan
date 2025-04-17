package fr.metouais.pixelartisan.spigot.util;

import fr.metouais.pixelartisan.common.block.BlockPos;
import fr.metouais.pixelartisan.common.block.IBlock;
import fr.metouais.pixelartisan.common.util.World;
import fr.metouais.pixelartisan.spigot.block.BlockSpigot;

public class WorldSpigot implements World {
    private final org.bukkit.World world;
    private WorldSpigot(org.bukkit.World world) {
        this.world = world;
    }
    public static WorldSpigot of(org.bukkit.World world) {
        return new WorldSpigot(world);
    }

    @Override
    public void ensureChunkIsLoaded(int chunkX, int chunkZ) {
        var chunk = world.getChunkAt(chunkX, chunkZ);
        if (!chunk.isLoaded()) chunk.load(true);
    }

    @Override
    public void setBlock(BlockPos pos, IBlock block) {
        world.getBlockAt(pos.getX(), pos.getY(), pos.getZ()).setType(BlockSpigot.cast(block).getMaterial());
    }
}
