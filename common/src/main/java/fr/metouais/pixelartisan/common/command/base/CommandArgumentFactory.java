package fr.metouais.pixelartisan.common.command.base;

public interface CommandArgumentFactory {
    CommandArgument<String> wordArgument(String name);
    default CommandArgument<Integer> integerArgument(String name) {
        return integerArgument(name, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    default CommandArgument<Integer> integerArgument(String name, int min) {
        return integerArgument(name, min, Integer.MAX_VALUE);
    }
    CommandArgument<Integer> integerArgument(String name, int min, int max);
}
