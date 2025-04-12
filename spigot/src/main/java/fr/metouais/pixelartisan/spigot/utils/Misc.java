package fr.metouais.pixelartisan.spigot.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;

public class Misc {
    private Misc() {}

    public static final int CHUNK_POWER=4;
    public static final int CHUNK_LENGTH=1<<CHUNK_POWER;
    public static final int CHUNK_SIZE=CHUNK_LENGTH*CHUNK_LENGTH;
    public static final Material[] MATERIALS = Material.values();

    public static String getMCVersion() {
        String version = Bukkit.getVersion();
        return version.substring(version.indexOf("(MC: ")+5, version.indexOf(")"));
    }
}
