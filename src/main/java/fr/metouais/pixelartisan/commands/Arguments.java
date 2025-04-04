package fr.metouais.pixelartisan.commands;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.CustomArgument.*;
import dev.jorel.commandapi.arguments.StringArgument;
import fr.metouais.pixelartisan.utils.FileUtils;

import java.nio.file.*;

public class Arguments {
    public static Argument<Path> FileArgument(String nodeName, Path folder) {
        if (FileUtils.isFolderEmpty(folder)) throw new IllegalArgumentException("Folder does not exist");
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            Path file = folder.resolve(info.input());
            if (!Files.exists(file) || !Files.isRegularFile(file)) {
                throw CustomArgumentException.fromMessageBuilder(new MessageBuilder("Invalid file : ").appendArgInput());
            }
            return file;
        })
        .replaceSuggestions(ArgumentSuggestions.strings(info -> folder.toFile().list()));
    }
}
