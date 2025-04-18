package fr.metouais.pixelartisan.fabric.block;

import fr.metouais.pixelartisan.common.block.BlockPos;

public class BlockPosFabric implements BlockPos {
    private net.minecraft.util.math.BlockPos pos;

    private BlockPosFabric(net.minecraft.util.math.BlockPos pos) {
        this.pos = pos;
    }

    public static BlockPosFabric of(net.minecraft.util.math.BlockPos pos) {
        return new BlockPosFabric(pos);
    }
    public static BlockPosFabric of(int x, int y, int z) {
        return BlockPosFabric.of(new net.minecraft.util.math.BlockPos(x, y, z));
    }

    public static BlockPosFabric cast(BlockPos pos) {
        if (pos instanceof BlockPosFabric posFabric) return posFabric;
        throw new IllegalArgumentException(BlockPos.class.getSimpleName()+" is not a "+BlockPosFabric.class.getSimpleName());
    }

    @Override
    public BlockPos clone() {
        return of(new net.minecraft.util.math.BlockPos(pos));
    }

    @Override
    public BlockPos add(BlockPos pos) {
        this.pos = this.pos.add(cast(pos).pos);
        return this;
    }

    @Override
    public BlockPos shl(int bits) {
        return BlockPosFabric.of(new net.minecraft.util.math.BlockPos(pos.getX()<<bits, pos.getY()<<bits, pos.getZ()<<bits));
    }

    @Override
    public int getX() {
        return pos.getX();
    }

    @Override
    public int getY() {
        return pos.getY();
    }

    @Override
    public int getZ() {
        return pos.getZ();
    }

    public net.minecraft.util.math.BlockPos getPos() {
        return pos;
    }
}
