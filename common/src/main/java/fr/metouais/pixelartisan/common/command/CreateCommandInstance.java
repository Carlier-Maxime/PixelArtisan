package fr.metouais.pixelartisan.common.command;

import fr.metouais.pixelartisan.common.PixelArtisan;
import fr.metouais.pixelartisan.common.block.Block;
import fr.metouais.pixelartisan.common.block.BlockPos;
import fr.metouais.pixelartisan.common.block.IBlock;
import fr.metouais.pixelartisan.common.command.base.CommandContext;
import fr.metouais.pixelartisan.common.util.*;
import fr.metouais.pixelartisan.common.data.DataManager;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class CreateCommandInstance implements Runnable{
    private static final long timeBetweenMsg = 5_000_000_000L;
    private static final Runnable POISON = () -> {};

    private final MessageSender sender;
    private final boolean flat;
    private int blockPlaced;
    private long timeForMsg;
    private final World world;
    private BlockPos pos;
    private final BlockPos directionW;
    private final BlockPos directionH;
    private final DataManager dataManager;
    private final BufferedImage img;
    private final byte face;
    private int nbBlock;
    private final int nbThreads;
    private final ExecutorService executor;
    private final BlockingQueue<Runnable> jobQueue;
    private CountDownLatch latch;
    private final Semaphore semChunkLimitInOneTick;
    private final Runnable taskTimerOneTick;

    public CreateCommandInstance(@NotNull CommandContext ctx, BlockPos start, BlockPos dirH, BlockPos dirW, byte face, BufferedImage img, int nbThreads) {
        sender = ctx.getSender();
        world = ctx.getWorld();
        dataManager = new DataManager(sender);
        pos = start.clone();
        directionW = dirW;
        directionH = dirH;
        this.face = face;
        this.img = img;
        this.flat = dirH.getY()==0 && dirW.getY()==0;
        this.nbThreads = nbThreads;
        executor = Executors.newFixedThreadPool(nbThreads);
        jobQueue = new LinkedBlockingQueue<>();
        int chunkLimitInOneTick = nbThreads<<2;
        semChunkLimitInOneTick = new Semaphore(chunkLimitInOneTick, true);
        taskTimerOneTick = TaskScheduler.get().runTaskSyncEveryTick(() -> {
            synchronized (semChunkLimitInOneTick) {
                semChunkLimitInOneTick.drainPermits();
                semChunkLimitInOneTick.release(chunkLimitInOneTick);
            }
        });
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
        TaskScheduler.get().stopTaskSyncEveryTick(taskTimerOneTick);
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
            var pos2 = pos.clone();
            for (int j=0; j<img.getWidth(); j+=Misc.CHUNK_LENGTH){
                int finalI = i;
                int finalJ = j;
                var finalPos = pos.clone();
                jobQueue.add(() -> buildChunk(finalPos,finalI,finalJ));
                pos.add(directionW.shl(Misc.CHUNK_POWER));
            }
            pos = pos2.clone();
            pos.add(directionH.shl(Misc.CHUNK_POWER));
        }
        stopWorkers();
        String duration = TimeUtils.formatDuration(System.nanoTime() - startTime);
        TaskScheduler.get().runTaskInMainThreadAndWait(() -> {
            MessageSender.CONSOLE.send("finish in "+duration+". ("+blockPlaced+" block placed)");
            sender.send("§2pixel art created in "+duration+"! ("+blockPlaced+" block placed)");
        });
    }

    synchronized
    private void progressMessage(){
        long time = System.nanoTime();
        if (time > timeForMsg){
            double perc = (blockPlaced*1.0/nbBlock)*100;
            MessageSender.CONSOLE.send(String.format("%.1f %%", perc));
            sender.send(String.format("%.1f %% (%d/%d)", perc, blockPlaced, nbBlock));
            timeForMsg = System.nanoTime() + timeBetweenMsg;
        }
    }

    private void buildChunk(BlockPos pos, int i, int j){
        class Entry {
            public BlockPos pos;
            public IBlock block = Block.air();
        }
        List<Entry> states = new ArrayList<>(Misc.CHUNK_SIZE);
        for (int k = 0; k < Misc.CHUNK_SIZE; k++) {
            states.add(new Entry());
        }
        var posBase = pos.clone();
        BlockPos posH;
        int index=0;
        for (int y = i; y > i-Misc.CHUNK_LENGTH; y--){
            if (y < 0) break;
            posH = posBase.clone();
            for (int x = j; x < j+Misc.CHUNK_LENGTH; x++){
                if (x >= img.getWidth()) break;
                states.get(index).pos =posBase.clone();
                states.get(index).block = dataManager.getBestBlock(img.getRGB(x, y), face, flat);
                posBase.add(directionW);
                incNbBlockPlaced();
                index++;
            }
            posBase = posH.clone().add(directionH);
        }
        int finalIndex = index;
        try {
            semChunkLimitInOneTick.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        TaskScheduler.get().runTaskInMainThreadAndWait(() -> {
            for (int ind = 0; ind< finalIndex; ind++) {
                var localPos = states.get(ind).pos;
                world.ensureChunkIsLoaded(localPos);
                world.setBlock(localPos, states.get(ind).block);
            }
            progressMessage();
        });
    }
}
