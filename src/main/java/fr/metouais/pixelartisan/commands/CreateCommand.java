package fr.metouais.pixelartisan.commands;

import fr.metouais.pixelartisan.PixelArtisan;
import fr.metouais.pixelartisan.Utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
    private CommandSender sender;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        this.sender = sender;
        if (!argsIsValid(args)) {
            ChatUtils.sendMessage(sender,"§c/pa create [direction] [filename] [size] (x) (y) (z)");
            return false;
        }
        ChatUtils.sendMessage(sender,"§ecalculation of direction, face and position");
        byte[] dirH = getDirectionH(args[0]);
        byte[] dirW = getDirectionW(args[0]);
        if (dirH==null || dirW==null) return false;
        byte face = getFace(args[0]);
        Location startLocation = getStartLocation(args);
        if (startLocation==null) return false;
        BufferedImage img = resizeImg(PixelArtisan.PATH_IMAGES+"/"+args[1],Integer.parseInt(args[2]));
        if (img==null) return false;
        ChatUtils.sendMessage(sender,"§ecreate pixel art..");
        ChatUtils.sendMessage(sender,"paint size : "+img.getWidth()+" "+img.getHeight());
        PixelArtisan.getInstance().getExecutorService().submit(new CreateCommandInstance(sender, startLocation, dirH, dirW, face, img));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        this.sender = sender;
        if (args.length<=1 && (args.length==0 || Arrays.stream(direction).noneMatch(val -> val.equals(args[0]))))
            return Arrays.asList(direction);
        if (args.length<=2){
            String[] list = PixelArtisan.PATH_IMAGES.toFile().list();
            if (args.length==2 && list!=null){
                List<String> l = List.of(list);
                if (!l.contains(args[1])) return l;
            } else {
                if (list==null) return null;
                return List.of(list);
            }
        }
        if (args.length==4) return List.of("~","~ ~","~ ~ ~");
        if (args.length==5) return List.of("~","~ ~");
        if (args.length==6) return List.of("~");
        return null;
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

        String[] tab = PixelArtisan.PATH_IMAGES.toFile().list();
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
            if (isInvalidCoordinate(args[3])){
                ChatUtils.sendMessage(sender,"§cinvalid abscissa (x)");
                return false;
            }
        }

        if (args.length>=5){
            if (isInvalidCoordinate(args[4])){
                ChatUtils.sendMessage(sender,"§cinvalid ordinate (y)");
                return false;
            }
        }

        if (args.length>=6){
            if (isInvalidCoordinate(args[5])){
                ChatUtils.sendMessage(sender,"§cinvalid dimension (z)");
                return false;
            }
        }

        return true;
    }

    private boolean isInvalidCoordinate(String X){
        return !isValidCoordinate(X);
    }

    private boolean isValidCoordinate(String x){
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
            BufferedImage img = getBufferedImage(size, originalImg);
            Graphics2D g2D = img.createGraphics();
            g2D.drawImage(originalImg,0,0,img.getWidth(),img.getHeight(),null);
            g2D.dispose();
            return img;
        } catch (IOException e) {
            ChatUtils.sendMessage(sender,"§ccheck that the provided file is an image and that it is not corrupted");
            return null;
        }
    }

    private static BufferedImage getBufferedImage(int size, BufferedImage originalImg) {
        BufferedImage img;
        if (originalImg.getHeight()> originalImg.getWidth()){
            int w = (int) (originalImg.getWidth()*(size *1.0/ originalImg.getHeight()));
            if (w<=0) w=1;
            img = new BufferedImage(w, size,BufferedImage.TYPE_INT_ARGB);
        } else {
            int h = (int) (originalImg.getHeight()*(size *1.0/ originalImg.getWidth()));
            if (h<=0) h=1;
            img = new BufferedImage(size,h,BufferedImage.TYPE_INT_ARGB);
        }
        return img;
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
