package fr.metouais.pixelartisan.spigot.block;

import fr.metouais.pixelartisan.common.block.BlockPos;
import org.bukkit.Location;

public class BlockPosSpigot implements BlockPos {
    Location location;

    private BlockPosSpigot(Location location) {
        this.location = location;
    }

    public static BlockPosSpigot of(Location location) {
        return new BlockPosSpigot(location);
    }
    public static BlockPosSpigot of(int x, int y, int z) {
        return new BlockPosSpigot(new Location(null, x, y, z));
    }

    @Override
    public BlockPos clone() {
        return BlockPosSpigot.of(location.clone());
    }

    public static BlockPosSpigot cast(BlockPos pos) {
        if (pos instanceof BlockPosSpigot posSpigot) return posSpigot;
        throw new IllegalArgumentException(BlockPos.class.getSimpleName()+" is not a "+BlockPosSpigot.class.getSimpleName());
    }

    @Override
    public BlockPos add(BlockPos pos) {
        location.add(pos.getX(), pos.getY(), pos.getZ());
        return this;
    }

    @Override
    public BlockPos shl(int bits) {
        return BlockPosSpigot.of(new Location(location.getWorld(), getX()<<bits, getY()<<bits, getZ()<<bits));
    }

    @Override
    public int getX() {
        return location.getBlockX();
    }

    @Override
    public int getY() {
        return location.getBlockY();
    }

    @Override
    public int getZ() {
        return location.getBlockZ();
    }

    public Location getLocation() {
        return location;
    }
}
