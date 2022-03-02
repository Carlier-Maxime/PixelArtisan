package fr.metouais.pixelartisan.commands;

import fr.metouais.pixelartisan.PixelArtisan;
import fr.metouais.pixelartisan.Utils.ChatUtils;
import fr.metouais.pixelartisan.Utils.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CreateCommand extends MyCommand{
    private static final String[] direction = new String[]{"North","East","South","West","FlatNorthEast","FlatEastSouth","FlatSouthWest","FlatWestNorth"};
    private static final int wait = 20;
    private static final int chunckBeforeMsg = 4;

    private CommandSender sender;
    private boolean flat;
    private int blockPlaced;
    private int nbThread;
    private int chunckCounter;
    private byte[] directionW;
    private byte[] directionH;
    private DataManager dataManager;
    private BufferedImage img;
    private byte face;
    private int nbBlock;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        this.sender = sender;
        if (!argsIsValid(args)) {
            ChatUtils.sendMessage(sender,"§c/pa create [direction] [filename] [size] (x) (y) (z)");
            return false;
        }
        img = resizeImg("./plugins/PixelArtisan/images/"+args[1],Integer.parseInt(args[2]));
        if (img==null) return false;
        ChatUtils.sendMessage(sender,"§ecalculation of direction, face and position");
        if (args[0].contains("Flat")) flat = true;
        directionH = getDirectionH(args[0]);
        directionW = getDirectionW(args[0]);
        if (directionH==null || directionW==null) return false;
        face = getFace(args[0]);
        Location startLocation = getStartLocation(args);
        if (startLocation==null) return false;
        ChatUtils.sendMessage(sender,"§ecreate pixel art..");
        dataManager = new DataManager(sender);
        Location location = new Location(startLocation.getWorld(),startLocation.getBlockX(),startLocation.getBlockY(),startLocation.getBlockZ());
        nbBlock = img.getHeight()*img.getWidth();
        blockPlaced=0;
        chunckCounter=0;
        nbThread = 0;
        ChatUtils.sendMessage(sender,"paint size : "+img.getWidth()+" "+img.getHeight());
        for (int i=img.getHeight()-1; i>=0; i-=16){
            Location loc2 = new Location(location.getWorld(),location.getBlockX(),location.getBlockY(),location.getBlockZ());
            for (int j=0; j<img.getWidth(); j+=16){
                int finalI = i;
                int finalJ = j;
                final Location locC = location.clone();
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        buildChunck(locC,finalI,finalJ);
                    }
                }.runTaskLater(PixelArtisan.getInstance(), (long) wait*(nbThread+1));
                nbThread++;
                location.add(directionW[0]*16,directionW[1]*16,directionW[2]*16);
            }
            location = new Location(loc2.getWorld(),loc2.getBlockX(),loc2.getBlockY(),loc2.getBlockZ());
            location.add(directionH[0]*16,directionH[1]*16,directionH[2]*16);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        this.sender = sender;
        if (args.length<=1 && (args.length==0 || Arrays.stream(direction).noneMatch(val -> val.equals(args[0]))))
            return Arrays.asList(direction);
        if (args.length<=2){
            String[] list = new File("./plugins/PixelArtisan/images").list();
            if (args.length==2 && list!=null){
                List<String> l = List.of(list);
                if (!l.contains(args[1])) return l;
            } else {
                if (list==null) return null;
                return List.of(list);
            }
        }
        if (args.length==4) return List.of("~","~ ~","~ ~ ~");
        return null;
    }

    private void buildChunck(Location loc, int i, int j){
        Location locBase = loc.clone();
        Location locH;
        ChatUtils.sendConsoleMessage("Final J : I = "+j+" : "+i);
        for (int y = i; y > i -16; y--){
            if (y < 0) break;
            locH = new Location(locBase.getWorld(),locBase.getBlockX(),locBase.getBlockY(),locBase.getBlockZ());
            for (int x = j; x< j +16; x++){
                if (x >= img.getWidth()) break;
                ChatUtils.sendConsoleMessage("x : y = "+x+" : "+y);
                Material material = Material.values()[dataManager.getBestMaterial(img.getRGB(x, y),face, flat)];
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
            ChatUtils.sendConsoleMessage(perc+" %");
            ChatUtils.sendMessage(sender,perc+" % ("+blockPlaced+"/"+nbBlock+")");
            chunckCounter=0;
        }
        if (blockPlaced==nbBlock){
            flat = false;
            ChatUtils.sendConsoleMessage("finish. ("+blockPlaced+" block placed)");
            ChatUtils.sendMessage(sender,"§2pixel art created ! ("+blockPlaced+" block placed)");
        }
        nbThread--;
    }

    private boolean argsIsValid(String[] args){
        if (args.length<3) {
            ChatUtils.sendMessage(sender,"§cmissing argument");
            return false;
        }

        if (!List.of(direction).contains(args[0])){
            ChatUtils.sendMessage(sender,"§cdirection invalid");
            return false;
        }

        String[] tab = new File("./plugins/PixelArtisan/images").list();
        if (tab==null) {
            ChatUtils.sendMessage(sender,"§cno images are present in the images folder of the plugin");
            return false;
        }
        else if (!List.of(tab).contains(args[1])){
            ChatUtils.sendMessage(sender,"§cthe specified file can not be found");
        }

        try {
            int x = Integer.parseInt(args[2]);
            if (x<=0){
                ChatUtils.sendMessage(sender,"§csize must be positive");
            }
        } catch (Exception e){
            ChatUtils.sendMessage(sender,"§csize must be an integer");
            return false;
        }

        if (args.length>=4){
            if (!verifyCoordinates(args[3])){
                ChatUtils.sendMessage(sender,"§cinvalid abscissa (x)");
                return false;
            }
        }

        if (args.length>=5){
            if (!verifyCoordinates(args[4])){
                ChatUtils.sendMessage(sender,"§cinvalid ordinate (y)");
                return false;
            }
        }

        if (args.length>=6){
            if (!verifyCoordinates(args[5])){
                ChatUtils.sendMessage(sender,"§cinvalid dimension (z)");
                return false;
            }
        }

        return true;
    }

    private boolean verifyCoordinates(String x){
        String val;
        if (x.startsWith("~")){
            String[] split = x.split("~");
            if (split.length<=1) return true;
            else val = split[1];
        } else val = x;
        try {
            Integer.parseInt(val);
        } catch (Exception e){
            return false;
        }
        return true;
    }

    private BufferedImage resizeImg(String originalImgPath, int size){
        ChatUtils.sendMessage(sender,"§eimage recovery and resizing..");
        try {
            BufferedImage originalImg = ImageIO.read(new File(originalImgPath));
            BufferedImage img;
            if (originalImg.getHeight()> originalImg.getWidth()){
                int w = (int) (originalImg.getWidth()*(size*1.0/originalImg.getHeight()));
                if (w<=0) w=1;
                img = new BufferedImage(w,size,BufferedImage.TYPE_INT_ARGB);
            } else {
                int h = (int) (originalImg.getHeight()*(size*1.0/originalImg.getWidth()));
                if (h<=0) h=1;
                img = new BufferedImage(size,h,BufferedImage.TYPE_INT_ARGB);
            }
            Graphics2D g2D = img.createGraphics();
            g2D.drawImage(originalImg,0,0,img.getWidth(),img.getHeight(),null);
            g2D.dispose();
            return img;
        } catch (IOException e) {
            ChatUtils.sendMessage(sender,"§ccheck that the provided file is an image and that it is not corrupted");
            return null;
        }
    }

    private byte[] getDirectionH(String direction){
        // "North","East","South","West","FlatNorthEast","FlatEastSouth","FlatSouthWest","FlatWestNorth"
        for (int i=0; i<CreateCommand.direction.length; i++){
            if (Objects.equals(CreateCommand.direction[i], direction)){
                return switch (i) {
                    case 0, 1, 2, 3 -> new byte[]{0, 1, 0};
                    case 4 -> new byte[]{0, 0, -1};
                    case 5 -> new byte[]{1, 0, 0};
                    case 6 -> new byte[]{0, 0, 1};
                    case 7 -> new byte[]{-1, 0, 0};
                    default -> null;
                };
            }
        }
        return null;
    }

    private byte[] getDirectionW(String direction){
        // "North","East","South","West","FlatNorthEast","FlatEastSouth","FlatSouthWest","FlatWestNorth"
        for (int i=0; i<CreateCommand.direction.length; i++){
            if (Objects.equals(CreateCommand.direction[i], direction)){
                return switch (i) {
                    case 0,4 -> new byte[]{1, 0, 0};
                    case 1,5 -> new byte[]{0, 0, 1};
                    case 2,6 -> new byte[]{-1, 0, 0};
                    case 3,7 -> new byte[]{0, 0, -1};
                    default -> null;
                };
            }
        }
        return null;
    }

    private Location getStartLocation(String[] args){
        Player player;
        if (sender instanceof Player){
            player = (Player) sender;
        } else player=null;
        if (args.length<4){
            if (player!=null) return player.getLocation();
            else {
                ChatUtils.sendMessage(sender,"§conly a player may not specify the starting position");
                return null;
            }
        } else {
            Integer x = getCoordinate(args[3], 0);
            Integer y;
            if (args.length<5) y=getCoordinate("~",1);
            else y=getCoordinate(args[4], 1);
            Integer z;
            if (args.length<6) z=getCoordinate("~",2);
            else z=getCoordinate(args[5], 2);
            if (x==null || y==null || z==null) return null;
            if (player==null) return new Location(Bukkit.getServer().getWorld("world"),x,y,z);
            else return new Location(player.getWorld(), x,y,z);
        }
    }

    private Integer getCoordinate(String co, int i){
        int x=0;
        if (co.contains("~")){
            if (sender instanceof Player player){
                switch (i){
                    case 0 -> x+=player.getLocation().getBlockX();
                    case 1 -> x+=player.getLocation().getBlockY();
                    case 2 -> x+=player.getLocation().getBlockZ();
                }
            } else {
                ChatUtils.sendMessage(sender,"§conly a player can use '~' in starting coordinates");
                return null;
            }
        }
        String[] split = co.split("~");
        if (split.length<=1) return x;
        x+=Integer.parseInt(split[1]);
        return x;
    }

    private byte getFace(String direction){
        // "North","East","South","West","FlatNorthEast","FlatEastSouth","FlatSouthWest","FlatWestNorth"
        return switch (direction){
            case "North" -> (byte) 1;
            case "East" -> (byte) 2;
            case "South" -> (byte) 3;
            case "West" -> (byte) 4;
            default -> (byte) 0;
        };
    }
}
