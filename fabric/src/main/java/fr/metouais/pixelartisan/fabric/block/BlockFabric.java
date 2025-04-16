package fr.metouais.pixelartisan.fabric.block;

import fr.metouais.pixelartisan.common.block.IBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FallingBlock;
import net.minecraft.registry.Registries;

public class BlockFabric implements IBlock {
    private final Block block;

    private BlockFabric(Block block){
        this.block = block;
    }

    public static BlockFabric of(Block block) {
        return new BlockFabric(block);
    }

    @Override
    public boolean hasGravity() {
        return block instanceof FallingBlock;
    }

    @Override
    public boolean isOpaque() {
        return block.getDefaultState().isOpaque();
    }

    @Override
    public String getId() {
        return Registries.BLOCK.getId(block).toString();
    }
}
