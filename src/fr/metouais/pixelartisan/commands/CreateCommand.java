package fr.metouais.pixelartisan.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        this.sender = sender;
        if (!argsIsValid(args)) {
            sender.sendMessage("§c/pa create [direction] [filename] [size] (x) (y) (z)");
            return false;
        }
        BufferedImage img = resizeImg("./plugins/PixelArtisan/images/"+args[1],Integer.parseInt(args[2]));
        if (img==null) return false;
        byte[] directionH = getDirectionH(args[0]);
        byte[] directionW = getDirectionW(args[0]);
        if (directionH==null || directionW==null) return false;
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

    private boolean argsIsValid(String[] args){
        if (args.length<3) {
            sender.sendMessage("§cmissing argument");
            return false;
        }

        if (!List.of(direction).contains(args[0])){
            sender.sendMessage("§cdirection invalid");
            return false;
        }

        String[] tab = new File("./plugins/PixelArtisan/images").list();
        if (tab==null) {
            sender.sendMessage("§cno images are present in the images folder of the plugin");
            return false;
        }
        else if (!List.of(tab).contains(args[1])){
            sender.sendMessage("§cthe specified file can not be found");
        }

        try {
            int x = Integer.parseInt(args[2]);
            if (x<=0){
                sender.sendMessage("§csize must be positive");
            }
        } catch (Exception e){
            sender.sendMessage("§csize must be an integer");
            return false;
        }

        if (args.length>=4){
            if (!verifyCoordinates(args[3])){
                sender.sendMessage("§cinvalid abscissa (x)");
                return false;
            }
        }

        if (args.length>=5){
            if (!verifyCoordinates(args[4])){
                sender.sendMessage("§cinvalid ordinate (y)");
                return false;
            }
        }

        if (args.length>=6){
            if (!verifyCoordinates(args[5])){
                sender.sendMessage("§cinvalid dimension (z)");
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
        sender.sendMessage("§eimage recovery and resizing..");
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
            sender.sendMessage("§ccheck that the provided file is an image and that it is not corrupted");
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
}
