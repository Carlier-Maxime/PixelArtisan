package fr.metouais.pixelartisan.fabric.command.base;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import fr.metouais.pixelartisan.common.command.base.CommandArgument;
import fr.metouais.pixelartisan.common.command.base.CommandArgumentFactory;
import net.minecraft.server.command.CommandManager;

import java.util.List;

public class CommandArgumentFactoryFabric implements CommandArgumentFactory {
    @Override
    public CommandArgument<String> wordArgument(String name) {
        return CommandArgumentFabric.of(CommandManager.argument(name, StringArgumentType.word()));
    }

    @Override
    public CommandArgument<Integer> integerArgument(String name, int min, int max) {
        return CommandArgumentFabric.of(CommandManager.argument(name, IntegerArgumentType.integer(min, max)));
    }
}
