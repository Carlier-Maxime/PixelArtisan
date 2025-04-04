package fr.metouais.pixelartisan.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import fr.metouais.pixelartisan.PixelArtisan;
import com.google.gson.JsonObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final String VERSION_MANIFEST_URL = "https://launchermeta.mojang.com/mc/game/version_manifest.json";

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

    public static boolean isFolderEmpty(Path folder) {
        if (!Files.exists(folder) || !Files.isDirectory(folder)) return true;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
            boolean isEmpty = true;
            for (Path ignored : stream) {
                isEmpty = false;
                break;
            }

            if (isEmpty) return true;
        } catch (IOException e) {
            return true;
        }

        return false;
    }

    public static String downloadJson(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    public static void downloadFile(String url, Path outputPath) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        CLIENT.send(request, HttpResponse.BodyHandlers.ofFile(outputPath));
    }

    public static void downloadClientMC(String version, Path outputPath) throws Exception {
        String json = FileUtils.downloadJson(VERSION_MANIFEST_URL);
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
        JsonArray versionsArray = obj.getAsJsonArray("versions");
        for (JsonElement verElement : versionsArray) {
            JsonObject v = verElement.getAsJsonObject();
            if (!v.get("id").getAsString().equals(version)) continue;
            String versionUrl = v.get("url").getAsString();
            String versionJson = FileUtils.downloadJson(versionUrl);
            JsonObject versionObj = JsonParser.parseString(versionJson).getAsJsonObject();
            JsonObject downloads = versionObj.getAsJsonObject("downloads");
            JsonObject client = downloads.getAsJsonObject("client");
            String url = client.get("url").getAsString();
            if (url == null) {
                throw new RuntimeException("Unknown URL for download client MC " + version);
            }
            downloadFile(url, outputPath);
            ChatUtils.sendConsoleMessage("Download client MC complete for " + version);
            return;
        }
        throw new IllegalArgumentException("Unknown version: " + version);
    }

    public static void extractBlockTexturesFromClientMC(Path jarPath, Path outputDir) throws Exception {
        Files.createDirectories(outputDir);
        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(jarPath.toFile()))) {
            ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                if (entry.getName().startsWith("assets/minecraft/textures/block/")) {
                    Path filePath = outputDir.resolve(entry.getName().replace("assets/minecraft/textures/block/", ""));
                    Files.createDirectories(filePath.getParent());
                    try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zipIn.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
    }

    public static void extractBlockTexturesFromClientMC(String version, Path outputDir) throws Exception {
        Path tmpDir = Files.createTempDirectory(PixelArtisan.class.getSimpleName());
        Path jarPath = tmpDir.resolve("client.jar");
        downloadClientMC(version, jarPath);
        extractBlockTexturesFromClientMC(jarPath, outputDir);
        Files.deleteIfExists(jarPath);
        Files.deleteIfExists(tmpDir);
    }
}
