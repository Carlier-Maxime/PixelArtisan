package fr.metouais.pixelartisan.common.block;

public interface BlockPos {
    BlockPos clone();
    BlockPos add(BlockPos pos);
    BlockPos shl(int bits);
    int getX();
    int getY();
    int getZ();
}
