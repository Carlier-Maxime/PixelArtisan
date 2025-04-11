package fr.metouais.pixelartisan.commands;

import fr.metouais.pixelartisan.PixelArtisan;
import fr.metouais.pixelartisan.utils.ChatUtils;
import fr.metouais.pixelartisan.data.DataManager;
import fr.metouais.pixelartisan.utils.Misc;
import fr.metouais.pixelartisan.utils.TaskUtils;
import fr.metouais.pixelartisan.utils.TimeUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class CreateCommandInstance implements Runnable{
    private static final long timeBetweenMsg = 5_000_000_000L;
    private static final Runnable POISON = () -> {};

    private final CommandSender sender;
    private final boolean flat;
    private int blockPlaced;
    private long timeForMsg;
    private Location location;
    private final byte[] directionW;
    private final byte[] directionH;
    private final DataManager dataManager;
    private final BufferedImage img;
    private final byte face;
    private int nbBlock;
    private final int nbThreads;
    private final ExecutorService executor;
    private final BlockingQueue<Runnable> jobQueue;
    private CountDownLatch latch;
    private final Semaphore semChunkLimitInOneTick;
    private final BukkitTask taskTimerOneTick;

    public CreateCommandInstance(@NotNull CommandSender sender, Location start, byte[] dirH, byte[] dirW, byte face, BufferedImage img, int nbThreads) {
        this.sender = sender;
        dataManager = new DataManager(sender);
        location = start.clone();
        directionW = dirW;
        directionH = dirH;
        this.face = face;
        this.img = img;
        this.flat = dirH[1]==0 && dirW[1]==0;
        this.nbThreads = nbThreads;
        executor = Executors.newFixedThreadPool(nbThreads);
        jobQueue = new LinkedBlockingQueue<>();
        int chunkLimitInOneTick = nbThreads<<2;
        semChunkLimitInOneTick = new Semaphore(chunkLimitInOneTick, true);
        taskTimerOneTick = new BukkitRunnable(){
            @Override
            public void run() {
                synchronized (semChunkLimitInOneTick) {
                    semChunkLimitInOneTick.drainPermits();
                    semChunkLimitInOneTick.release(chunkLimitInOneTick);
                }
            }
        }.runTaskTimer(PixelArtisan.getInstance(), 1, 1);
    }

    private void launchWorkers() {
        latch = new CountDownLatch(nbThreads);
        for (int i = 0; i < nbThreads; i++) {
            executor.submit(() -> {
                try {
                    while (true) {
                        Runnable job = jobQueue.take();
                        if (job == POISON) break;
                        job.run();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }
    }

    private void stopWorkers() {
        for (int i = 0; i < nbThreads; i++) jobQueue.add(POISON);
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        taskTimerOneTick.cancel();
        executor.shutdownNow();
    }

    synchronized
    private void incNbBlockPlaced() {
        blockPlaced++;
    }

    @Override
    public void run() {
        long startTime = System.nanoTime();
        timeForMsg = System.nanoTime() + timeBetweenMsg;
        nbBlock = img.getHeight()*img.getWidth();
        blockPlaced=0;
        launchWorkers();
        for (int i=img.getHeight()-1; i>=0; i-= Misc.CHUNK_LENGTH){
            Location loc2 = new Location(location.getWorld(),location.getBlockX(),location.getBlockY(),location.getBlockZ());
            for (int j=0; j<img.getWidth(); j+=Misc.CHUNK_LENGTH){
                int finalI = i;
                int finalJ = j;
                Location finalLoc = location.clone();
                jobQueue.add(() -> buildChunk(finalLoc,finalI,finalJ));
                location.add(directionW[0]<<Misc.CHUNK_POWER,directionW[1]<<Misc.CHUNK_POWER,directionW[2]<<Misc.CHUNK_POWER);
            }
            location = new Location(loc2.getWorld(),loc2.getBlockX(),loc2.getBlockY(),loc2.getBlockZ());
            location.add(directionH[0]<<Misc.CHUNK_POWER,directionH[1]<<Misc.CHUNK_POWER,directionH[2]<<Misc.CHUNK_POWER);
        }
        stopWorkers();
        String duration = TimeUtils.formatDuration(System.nanoTime() - startTime);
        TaskUtils.runTaskInMainThreadAndWait(() -> {
            ChatUtils.sendConsoleMessage("finish in "+duration+". ("+blockPlaced+" block placed)");
            ChatUtils.sendMessage(sender,"§2pixel art created in "+duration+"! ("+blockPlaced+" block placed)");
        });
    }

    synchronized
    private void progressMessage(){
        long time = System.nanoTime();
        if (time > timeForMsg){
            double perc = (blockPlaced*1.0/nbBlock)*100;
            ChatUtils.sendConsoleMessage(String.format("%.1f %%", perc));
            ChatUtils.sendMessage(sender,String.format("%.1f %% (%d/%d)", perc, blockPlaced, nbBlock));
            timeForMsg = System.nanoTime() + timeBetweenMsg;
        }
    }

    private void buildChunk(Location loc, int i, int j){
        class Entry {
            public Location loc;
            public Material material = Material.AIR;
        }
        List<Entry> states = new ArrayList<>(Misc.CHUNK_SIZE);
        for (int k = 0; k < Misc.CHUNK_SIZE; k++) {
            states.add(new Entry());
        }
        Location locBase = loc.clone();
        Location locH;
        int index=0;
        for (int y = i; y > i-Misc.CHUNK_LENGTH; y--){
            if (y < 0) break;
            locH = locBase.clone();
            for (int x = j; x < j+Misc.CHUNK_LENGTH; x++){
                if (x >= img.getWidth()) break;
                states.get(index).loc=locBase.clone();
                states.get(index).material = Misc.MATERIALS[dataManager.getBestMaterial(img.getRGB(x, y), face, flat)];
                locBase.add(directionW[0],directionW[1],directionW[2]);
                incNbBlockPlaced();
                index++;
            }
            locBase = locH.clone().add(directionH[0],directionH[1],directionH[2]);
        }
        int finalIndex = index;
        try {
            semChunkLimitInOneTick.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        TaskUtils.runTaskInMainThreadAndWait(() -> {
            for (int ind = 0; ind< finalIndex; ind++) {
                var localLoc = states.get(ind).loc;
                if (!localLoc.getChunk().isLoaded()) localLoc.getChunk().load();
                localLoc.getBlock().setType(states.get(ind).material, false);
            }
            progressMessage();
        });
    }
}
