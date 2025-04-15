package fr.metouais.pixelartisan.common.command.base;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public interface CommandArgumentFactory {
    CommandArgument<String> wordArgument(String name);
    default CommandArgument<Integer> integerArgument(String name) {
        return integerArgument(name, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    default CommandArgument<Integer> integerArgument(String name, int min) {
        return integerArgument(name, min, Integer.MAX_VALUE);
    }
    CommandArgument<Integer> integerArgument(String name, int min, int max);
    default CommandArgument<Path> fileArgument(String name, Path folder, List<Predicate<Path>> conditions) {
        return new CommandCustomArgument<>(wordArgument(name), input -> {
            Path file = folder.resolve(input);
            if (conditions.stream().allMatch(cond -> cond.test(file))) return file;
            else throw new CommandException("Invalid file : "+input);
        }, Objects::toString, String.class);
    }
}
