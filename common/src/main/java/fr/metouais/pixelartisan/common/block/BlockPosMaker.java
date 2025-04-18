package fr.metouais.pixelartisan.common.block;

import fr.metouais.pixelartisan.common.util.TriFunction;
import org.jetbrains.annotations.NotNull;

public class BlockPosMaker {
    private static @NotNull TriFunction<Integer, Integer, Integer, @NotNull BlockPos> maker = (_1, _2, _3) -> {
        throw new UnsupportedOperationException();
    };
    public static void setMaker(@NotNull TriFunction<Integer, Integer, Integer, @NotNull BlockPos> maker) {
        BlockPosMaker.maker = maker;
    }
    public static @NotNull BlockPos make(int x, int y, int z) {
        return maker.apply(x,y,z);
    }
}
