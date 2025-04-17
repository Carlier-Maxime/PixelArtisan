package fr.metouais.pixelartisan.common.block;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IBlockManager {
    IBlock of(@NotNull String id);
    List<IBlock> all();
    IBlock air();
}
