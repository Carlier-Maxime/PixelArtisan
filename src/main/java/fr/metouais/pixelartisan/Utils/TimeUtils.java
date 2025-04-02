package fr.metouais.pixelartisan.Utils;

import java.time.Duration;

public class TimeUtils {
    public static String formatDuration(long nanos) {
        Duration duration = Duration.ofNanos(nanos);

        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        long millis = duration.toMillisPart();

        if (days > 0) return String.format("%d d %02d h %02d min", days, hours, minutes);
        if (hours > 0) return String.format("%02d h %02d min %02d s", hours, minutes, seconds);
        if (minutes > 0) return String.format("%02d min %02d s", minutes, seconds);
        if (seconds > 0) return String.format("%02d s %03d ms", seconds, millis);
        return String.format("%d ms", millis);
    }
}
