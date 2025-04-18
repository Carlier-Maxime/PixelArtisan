package fr.metouais.pixelartisan.spigot.util;

import fr.metouais.pixelartisan.common.util.ITaskScheduler;
import fr.metouais.pixelartisan.spigot.PixelArtisanSpigot;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.CountDownLatch;

public class TaskSchedulerSpigot implements ITaskScheduler {
    private record ContainBukkitTask(BukkitTask task) implements Runnable {
        @Override
        public void run() {}
    }

    @Override
    public void runTaskInMainThreadAndWait(Runnable task) {
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

    @Override
    public Runnable runTaskSyncEveryTick(Runnable task) {
        return new ContainBukkitTask(new BukkitRunnable(){
            @Override
            public void run() {
                task.run();
            }
        }.runTaskTimer(PixelArtisanSpigot.getInstance(), 1, 1));
    }

    @Override
    public void stopTaskSyncEveryTick(Runnable task) {
        if (task instanceof ContainBukkitTask container) container.task.cancel();
        else throw new IllegalArgumentException("task is not a BukkitRunnable");
    }
}
