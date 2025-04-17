package fr.metouais.pixelartisan.fabric.block;

import fr.metouais.pixelartisan.common.block.IBlock;
import fr.metouais.pixelartisan.common.block.IBlockManager;
import org.jetbrains.annotations.NotNull;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.block.Blocks;

import java.util.List;
import java.util.stream.Collectors;

public class BlockManagerFabric implements IBlockManager {
    private static final List<IBlock> blocks = Registries.BLOCK.stream().map(BlockFabric::of).collect(Collectors.toList());

    @Override
    public IBlock of(@NotNull String id) {
        return BlockFabric.of(Registries.BLOCK.get(Identifier.of(id)));
    }

    @Override
    public List<IBlock> all() {
        return blocks;
    }

    @Override
    public IBlock air() {
        return BlockFabric.of(Blocks.AIR);
    }
}
