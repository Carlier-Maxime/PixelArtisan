package fr.metouais.pixelartisan.fabric.util;

import fr.metouais.pixelartisan.common.block.BlockPos;
import fr.metouais.pixelartisan.common.block.IBlock;
import fr.metouais.pixelartisan.common.util.World;
import fr.metouais.pixelartisan.fabric.block.BlockFabric;
import fr.metouais.pixelartisan.fabric.block.BlockPosFabric;
import net.minecraft.block.Block;
import net.minecraft.world.chunk.ChunkStatus;

public class WorldFabric implements World {
    private final net.minecraft.world.World world;
    private WorldFabric(net.minecraft.world.World world) {
        this.world = world;
    }
    public static WorldFabric of(net.minecraft.world.World world) {
        return new WorldFabric(world);
    }

    @Override
    public void ensureChunkIsLoaded(int chunkX, int chunkZ) {
        world.getChunkManager().getChunk(chunkX, chunkZ, ChunkStatus.FULL, true);
    }

    @Override
    public void setBlock(BlockPos pos, IBlock block) {
        world.setBlockState(
            BlockPosFabric.cast(pos).getPos(),
            BlockFabric.cast(block).getBlock().getDefaultState(),
            Block.FORCE_STATE_AND_SKIP_CALLBACKS_AND_DROPS | Block.NOTIFY_LISTENERS
        );
    }
}
