package fr.metouais.pixelartisan.spigot.block;

import fr.metouais.pixelartisan.common.block.IBlock;
import fr.metouais.pixelartisan.common.block.IBlockManager;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BlockManagerSpigot implements IBlockManager {
    private static final List<IBlock> blocks = Arrays.stream(Material.values()).map(BlockSpigot::of).collect(Collectors.toList());

    @Override
    public IBlock of(@NotNull String id) {
        var mat = Material.matchMaterial(id);
        if (mat == null) return null;
        return BlockSpigot.of(mat);
    }

    @Override
    public List<IBlock> all() {
        return blocks;
    }
}
