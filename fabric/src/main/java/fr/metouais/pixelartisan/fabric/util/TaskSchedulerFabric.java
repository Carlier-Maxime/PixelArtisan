package fr.metouais.pixelartisan.fabric.util;

import fr.metouais.pixelartisan.common.util.ITaskScheduler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskSchedulerFabric implements ITaskScheduler {
    private static TaskSchedulerFabric instance;
    private final BlockingQueue<Runnable> jobQueue;
    private final List<Runnable> everyTickJobs;
    private MinecraftServer server;
    private TaskSchedulerFabric() {
        jobQueue = new LinkedBlockingQueue<>();
        everyTickJobs = new ArrayList<>();
        ServerTickEvents.END_SERVER_TICK.register(s -> {
            if (server == null) server = s;
            for (Runnable job : everyTickJobs) job.run();
            while (!jobQueue.isEmpty()) jobQueue.poll().run();
        });
    }
    public static TaskSchedulerFabric init() {
        instance = new TaskSchedulerFabric();
        return instance;
    }

    @Override
    public void runTaskInMainThreadAndWait(Runnable task) {
        if (server==null || server.isOnThread()) task.run();
        else {
            CountDownLatch latch = new CountDownLatch(1);
            jobQueue.add(() -> {
                    task.run();
                    latch.countDown();
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Runnable runTaskSyncEveryTick(Runnable task) {
        everyTickJobs.add(task);
        return task;
    }

    @Override
    public void stopTaskSyncEveryTick(Runnable task) {
        everyTickJobs.remove(task);
    }
}
