package fr.metouais.pixelartisan.common.command.base;

import fr.metouais.pixelartisan.common.block.BlockPos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface CommandArgumentFactory {
    CommandArgument<String> wordArgument(String name);
    default CommandArgument<Integer> integerArgument(String name) {
        return integerArgument(name, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    default CommandArgument<Integer> integerArgument(String name, int min) {
        return integerArgument(name, min, Integer.MAX_VALUE);
    }
    CommandArgument<Integer> integerArgument(String name, int min, int max);
    CommandArgument<BlockPos> blockPosArgument(String name);
    default CommandArgument<Path> fileArgument(String name, Path folder, List<Predicate<Path>> conditions) {
        if (!Files.isDirectory(folder)) throw new IllegalArgumentException("Folder does not exist or not directory: "+folder.toAbsolutePath());
        return new CommandCustomArgument<>(wordArgument(name), input -> {
            Path file = folder.resolve(input);
            if (conditions.stream().allMatch(cond -> cond.test(file))) return file;
            else throw new CommandException("Invalid file : "+input);
        }, Objects::toString, String.class).suggests(input -> {
            try (Stream<Path> stream = Files.list(folder)) {
                return stream
                    .filter(Files::exists)
                    .filter(path -> conditions.stream().allMatch(cond -> cond.test(path)))
                    .map(folder::relativize)
                    .toList();
            } catch (IOException e) {
                return List.of();
            }
        });
    }
}
