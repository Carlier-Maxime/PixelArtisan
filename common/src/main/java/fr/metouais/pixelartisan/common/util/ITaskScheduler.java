package fr.metouais.pixelartisan.common.util;

public interface ITaskScheduler {
    void runTaskInMainThreadAndWait(Runnable task);
    Runnable runTaskSyncEveryTick(Runnable task);
    void stopTaskSyncEveryTick(Runnable task);
}
