package fr.metouais.pixelartisan.commands;

import fr.metouais.pixelartisan.Utils.ChatUtils;
import fr.metouais.pixelartisan.Utils.DataManager;
import fr.metouais.pixelartisan.Utils.TaskUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;

public class CreateCommandInstance implements Runnable{
    private static final int chunckBeforeMsg = 4;

    private final CommandSender sender;
    private final boolean flat;
    private int blockPlaced;
    private int chunckCounter;
    private Location location;
    private final byte[] directionW;
    private final byte[] directionH;
    private final DataManager dataManager;
    private final BufferedImage img;
    private final byte face;
    private int nbBlock;

    public CreateCommandInstance(@NotNull CommandSender sender, Location start, byte[] dirH, byte[] dirW, byte face, BufferedImage img) {
        this.sender = sender;
        dataManager = new DataManager(sender);
        location = start.clone();
        directionW = dirW;
        directionH = dirH;
        this.face = face;
        this.img = img;
        this.flat = dirH[1]==0 && dirW[1]==0;
    }

    @Override
    public void run() {
        nbBlock = img.getHeight()*img.getWidth();
        blockPlaced=0;
        chunckCounter=0;
        for (int i=img.getHeight()-1; i>=0; i-=16){
            Location loc2 = new Location(location.getWorld(),location.getBlockX(),location.getBlockY(),location.getBlockZ());
            for (int j=0; j<img.getWidth(); j+=16){
                int finalI = i;
                int finalJ = j;
                final Location locC = location.clone();
                TaskUtils.runTaskInMainThreadAndWait(() -> buildChunck(locC,finalI,finalJ));
                location.add(directionW[0]*16,directionW[1]*16,directionW[2]*16);
            }
            location = new Location(loc2.getWorld(),loc2.getBlockX(),loc2.getBlockY(),loc2.getBlockZ());
            location.add(directionH[0]*16,directionH[1]*16,directionH[2]*16);
        }
        TaskUtils.runTaskInMainThreadAndWait(() -> {
            ChatUtils.sendConsoleMessage("finish. ("+blockPlaced+" block placed)");
            ChatUtils.sendMessage(sender,"§2pixel art created ! ("+blockPlaced+" block placed)");
        });
    }

    private void buildChunck(Location loc, int i, int j){
        Location locBase = loc.clone();
        Location locH;
        for (int y = i; y > i -16; y--){
            if (y < 0) break;
            locH = new Location(locBase.getWorld(),locBase.getBlockX(),locBase.getBlockY(),locBase.getBlockZ());
            for (int x = j; x< j +16; x++){
                if (x >= img.getWidth()) break;
                Material material = Material.values()[dataManager.getBestMaterial(img.getRGB(x, y), face, flat)];
                locBase.getBlock().setType(material);
                locBase.add(directionW[0],directionW[1],directionW[2]);
                blockPlaced++;
            }
            locBase = new Location(locH.getWorld(),locH.getBlockX(),locH.getBlockY(),locH.getBlockZ());
            locBase.add(directionH[0],directionH[1],directionH[2]);
        }
        chunckCounter++;
        if (chunckCounter==chunckBeforeMsg){
            double perc = (blockPlaced*1.0/nbBlock)*100;
            ChatUtils.sendConsoleMessage(String.format("%.1f %%", perc));
            ChatUtils.sendMessage(sender,String.format("%.1f %% (%d/%d)", perc, blockPlaced, nbBlock));
            chunckCounter=0;
        }
    }
}
