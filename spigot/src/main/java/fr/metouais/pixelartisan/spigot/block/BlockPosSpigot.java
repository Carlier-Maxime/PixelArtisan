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

    @Override
    public BlockPos clone() {
        return BlockPosSpigot.of(location);
    }

    public static BlockPosSpigot cast(BlockPos pos) {
        if (pos instanceof BlockPosSpigot posSpigot) return posSpigot;
        throw new IllegalArgumentException(BlockPos.class.getSimpleName()+" is not a "+BlockPosSpigot.class.getSimpleName());
    }

    @Override
    public BlockPos add(BlockPos pos) {
        location.add(cast(pos).location);
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
