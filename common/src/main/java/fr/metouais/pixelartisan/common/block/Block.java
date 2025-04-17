package fr.metouais.pixelartisan.common.block;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Block {
    private static Block instance = new Block(null){
        @Override
        protected IBlock _of(@NotNull String id) {
            throw new UnsupportedOperationException();
        }
        @Override
        protected List<IBlock> _all() {
            throw new UnsupportedOperationException();
        }
        @Override
        protected IBlock _air() {
            throw new UnsupportedOperationException();
        }
    };
    private Block(IBlockManager manager){
        this.manager = manager;
    }
    private final IBlockManager manager;
    public static void setManager(IBlockManager manager) {
        instance = new Block(manager);
    }
    public static IBlock of(@NotNull String id) {
        return instance._of(id);
    }
    public static List<IBlock> all() {
        return instance._all();
    }
    public static IBlock air() {
        return instance._air();
    }
    protected IBlock _of(@NotNull String id) {
        return manager.of(id);
    }
    protected List<IBlock> _all() {
        return manager.all();
    }
    protected IBlock _air() {
        return manager.air();
    }
}
