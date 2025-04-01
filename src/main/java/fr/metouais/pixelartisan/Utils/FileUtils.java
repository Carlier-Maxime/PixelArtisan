package fr.metouais.pixelartisan.Utils;

import fr.metouais.pixelartisan.PixelArtisan;

import java.io.IOException;
import java.nio.file.*;

public class FileUtils {
    public static void tryDelete(Path file){
        try {
            Files.delete(file);
        } catch (IOException e) {
            PixelArtisan.LOGGER.error("Failed to delete file: {} - {}", file, e.getMessage());
        }
    }

    public static void tryDeleteContentOfFolder(Path folder){
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(folder);
            for (Path file : stream) tryDelete(file);
        } catch (IOException e) {
            System.err.println("Failed to read directory: " + folder + " - " + e.getMessage());
        }
    }

    public static boolean isFolderNotEmpty(Path folder) {
        if (!Files.exists(folder) || !Files.isDirectory(folder)) return false;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
            boolean isEmpty = true;
            for (Path ignored : stream) {
                isEmpty = false;
                break;
            }

            if (isEmpty) return false;
        } catch (IOException e) {
            return false;
        }

        return true;
    }
}
