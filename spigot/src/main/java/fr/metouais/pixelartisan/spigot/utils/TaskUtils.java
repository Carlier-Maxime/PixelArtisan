package fr.metouais.pixelartisan.spigot.utils;

import fr.metouais.pixelartisan.spigot.PixelArtisanSpigot;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.CountDownLatch;

public class TaskUtils {
    private TaskUtils() {}

    public static void runTaskInMainThreadAndWait(Runnable task){
        if (Bukkit.isPrimaryThread()) task.run();
        else {
            CountDownLatch latch = new CountDownLatch(1);
            new BukkitRunnable(){
                @Override
                public void run() {
                    task.run();
                    latch.countDown();
                }
            }.runTask(PixelArtisanSpigot.getInstance());
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
