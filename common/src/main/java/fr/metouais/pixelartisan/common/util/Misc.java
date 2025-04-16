package fr.metouais.pixelartisan.common.util;

import java.util.function.Supplier;

public class Misc {
    private Misc() {}

    public static final int CHUNK_POWER=4;
    public static final int CHUNK_LENGTH=1<<CHUNK_POWER;
    public static final int CHUNK_SIZE=CHUNK_LENGTH*CHUNK_LENGTH;
    private static Supplier<String> getterMCVersion = () -> {throw new UnsupportedOperationException();};

    public static void setGetterMCVersion(Supplier<String> getterMCVersion) {
        Misc.getterMCVersion = getterMCVersion;
    }

    public static String getMCVersion() {
        return getterMCVersion.get();
    }
}
