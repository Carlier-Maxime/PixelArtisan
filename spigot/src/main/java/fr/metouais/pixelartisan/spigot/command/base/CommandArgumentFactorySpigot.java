package fr.metouais.pixelartisan.spigot.command.base;

import dev.jorel.commandapi.arguments.*;
import fr.metouais.pixelartisan.common.block.BlockPos;
import fr.metouais.pixelartisan.common.command.base.CommandArgument;
import fr.metouais.pixelartisan.common.command.base.CommandArgumentFactory;
import fr.metouais.pixelartisan.common.command.base.CommandCustomArgument;
import fr.metouais.pixelartisan.spigot.block.BlockPosSpigot;
import org.bukkit.Location;

public class CommandArgumentFactorySpigot implements CommandArgumentFactory {
    private static <T> CommandArgumentSpigot<T> arg(Argument<T> arg) {
        return CommandArgumentSpigot.of(arg);
    }

    @Override
    public CommandArgument<String> wordArgument(String name) {
        return arg(new StringArgument(name));
    }

    @Override
    public CommandArgument<Integer> integerArgument(String name, int min, int max) {
        return arg(new IntegerArgument(name, min, max));
    }

    @Override
    public CommandArgument<BlockPos> blockPosArgument(String name) {
        return new CommandCustomArgument<>(
            arg(new LocationArgument(name, LocationType.BLOCK_POSITION)),
            BlockPosSpigot::of,
            pos -> BlockPosSpigot.cast(pos).getLocation(),
            Location.class
        );
    }
}
