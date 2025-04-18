package fr.metouais.pixelartisan.spigot.block;

import fr.metouais.pixelartisan.common.block.IBlock;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class BlockSpigot implements IBlock {
    private final Material material;
    private BlockSpigot(@NotNull Material material) {
        this.material = material;
    }
    public static BlockSpigot of(@NotNull Material material) {
        return new BlockSpigot(material);
    }

    @Override
    public boolean hasGravity() {
        return material.hasGravity();
    }

    @Override
    public boolean isFullBlock() {
        return material.isOccluding();
    }

    @Override
    public String getId() {
        return material.name();
    }

    public Material getMaterial() {
        return material;
    }

    public static BlockSpigot cast(IBlock block) {
        if (block instanceof BlockSpigot blockSpigot) return blockSpigot;
        throw new ClassCastException(block.getClass().getName());
    }
}
