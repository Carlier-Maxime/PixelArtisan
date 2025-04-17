package fr.metouais.pixelartisan.fabric.command.base;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import fr.metouais.pixelartisan.common.block.BlockPos;
import fr.metouais.pixelartisan.common.command.base.CommandArgument;
import fr.metouais.pixelartisan.common.command.base.CommandArgumentFactory;
import fr.metouais.pixelartisan.common.command.base.CommandCustomArgument;
import fr.metouais.pixelartisan.fabric.block.BlockPosFabric;
import fr.metouais.pixelartisan.fabric.util.MessageSenderFabric;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.server.command.CommandManager;

public class CommandArgumentFactoryFabric implements CommandArgumentFactory {
    private <T> CommandArgumentFabric<T> arg(String name, ArgumentType<T> type) {
        return CommandArgumentFabric.of(CommandManager.argument(name, type));
    }

    @Override
    public CommandArgumentFabric<String> wordArgument(String name) {
        return arg(name, StringArgumentType.word());
    }

    @Override
    public CommandArgumentFabric<Integer> integerArgument(String name, int min, int max) {
        return arg(name, IntegerArgumentType.integer(min, max));
    }

    @Override
    public CommandArgument<BlockPos> blockPosArgument(String name) {
        return new CommandCustomArgument<>(
            arg(name, BlockPosArgumentType.blockPos()),
                (ctx, input) -> BlockPosFabric.of(input.toAbsoluteBlockPos(MessageSenderFabric.cast(ctx.getSender()).getSource())),
                (ctx, pos) -> null,
            PosArgument.class
        );
    }
}
