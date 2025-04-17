package fr.metouais.pixelartisan.common.util;

import fr.metouais.pixelartisan.common.block.BlockPos;
import fr.metouais.pixelartisan.common.block.IBlock;

public interface World {
    default void ensureChunkIsLoaded(BlockPos pos) {
        ensureChunkIsLoaded(pos.getX()>>Misc.CHUNK_POWER, pos.getZ()>>Misc.CHUNK_POWER);
    }
    void ensureChunkIsLoaded(int chunkX, int chunkZ);
    void setBlock(BlockPos pos, IBlock block);
}
