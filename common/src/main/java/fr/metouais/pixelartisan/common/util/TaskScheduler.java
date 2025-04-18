package fr.metouais.pixelartisan.common.util;

import org.jetbrains.annotations.NotNull;

public class TaskScheduler{
    private static @NotNull ITaskScheduler scheduler = new ITaskScheduler(){
        @Override
        public void runTaskInMainThreadAndWait(Runnable task) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Runnable runTaskSyncEveryTick(Runnable task) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void stopTaskSyncEveryTick(Runnable task) {
            throw new UnsupportedOperationException();
        }
    };
    private TaskScheduler() {}
    synchronized
    public static void set(@NotNull ITaskScheduler scheduler) {
        TaskScheduler.scheduler = scheduler;
    }
    public static @NotNull ITaskScheduler get() {
        return scheduler;
    }
}
