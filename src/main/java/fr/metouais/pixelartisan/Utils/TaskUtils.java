package fr.metouais.pixelartisan.Utils;

import fr.metouais.pixelartisan.PixelArtisan;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.CountDownLatch;

public class TaskUtils {
    public static void runTaskInMainThreadAndWait(Runnable task){
        CountDownLatch latch = new CountDownLatch(1);
        new BukkitRunnable(){
            @Override
            public void run() {
                task.run();
                latch.countDown();
            }
        }.runTask(PixelArtisan.getInstance());
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
