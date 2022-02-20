package fr.metouais.pixelartisan.commands;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class CreateCommand extends MyCommand{
    private static final String[] direction = new String[]{"North","East","South","West","FlatNorthEast","FlatEastSouth","FlatSouthWest","FlatWestNorth"};

    private CommandSender sender;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        this.sender = sender;
        if (!argsIsValid(args)) sender.sendMessage("§c/pa create [direction] [filename] [size] (x) (y) (z)");
        if (sender instanceof Player player){
            Block block = player.getLocation().getBlock();
            block.setType(Material.valueOf("AMETHYST_BLOCK"));
            sender.sendMessage("§2"+Material.AMETHYST_BLOCK.name()+" placed");
            return true;
        }
        return false;
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
            Integer.parseInt(args[2]);
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
}
