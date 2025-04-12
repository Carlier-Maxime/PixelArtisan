package fr.metouais.pixelartisan.spigot.command;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.CustomArgument.*;
import dev.jorel.commandapi.arguments.StringArgument;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Arguments {
    @SafeVarargs
    public static Argument<Path> FileArgument(String nodeName, Path folder, Predicate<Path>... conditions) {
        if (!Files.isDirectory(folder)) throw new IllegalArgumentException("Folder does not exist or not directory: "+folder.toAbsolutePath());
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            Path file = folder.resolve(info.input());
            if (Arrays.stream(conditions).allMatch(cond -> cond.test(file))) return file;
            else throw CustomArgumentException.fromMessageBuilder(new MessageBuilder("Invalid file : ").appendArgInput());
        })
        .replaceSuggestions(ArgumentSuggestions.strings(info -> {
            try (Stream<Path> stream = Files.list(folder)) {
                return stream
                        .filter(Files::exists)
                        .filter(path -> Arrays.stream(conditions).allMatch(cond -> cond.test(path)))
                        .map(folder::relativize)
                        .map(Path::toString)
                        .toArray(String[]::new);
            } catch (IOException e) {
                return new String[0];
            }
        }));
    }
}
