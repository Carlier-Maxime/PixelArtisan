package fr.metouais.pixelartisan.spigot.command.base;

import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import fr.metouais.pixelartisan.common.command.base.CommandArgument;
import fr.metouais.pixelartisan.common.command.base.CommandArgumentFactory;

public class CommandArgumentFactorySpigot implements CommandArgumentFactory {
    @Override
    public CommandArgument<String> wordArgument(String name) {
        return CommandArgumentSpigot.of(new StringArgument(name));
    }

    @Override
    public CommandArgument<Integer> integerArgument(String name, int min, int max) {
        return CommandArgumentSpigot.of(new IntegerArgument(name, min, max));
    }
}
