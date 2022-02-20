package fr.metouais.pixelartisan.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CustomTextureCommand extends MyCommand{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String pathCustomTexture = "./plugins/PixelArtisan/custom_texture";
        File dir = new File(pathCustomTexture);
        File[] list = dir.listFiles();
        if (list==null || list.length<=0) {
            sender.sendMessage("§ccustom_texture folder is empty ! (fill the folder and retry)");
            if (sender instanceof Player){
                String link = "https://github.com/Carlier-Maxime/PixelArtisan";
                sender.sendMessage("§6For more information : "+link);
            }
            return false;
        } else {
            sender.sendMessage("§echecking texture and delete unnecessary files...");
            int nbDelete=0;
            for (File file : list){
                if (!file.isFile()) file.delete();
                String[] nameSplit = file.getName().split("\\.");
                if (nameSplit[nameSplit.length-1].equals("mcmeta")){
                    file.delete();
                    new File(pathCustomTexture+"/"+nameSplit[0]+".png").delete();
                } else if (!nameSplit[nameSplit.length-1].equals("png")) file.delete();
                for (String s : new String[]{"destroy","_plant","grass","end_portal","composter","debug","chorus","bamboo"}){
                    if (nameSplit[0].contains(s)) file.delete();
                }
            }
            sender.sendMessage("§e"+nbDelete+" files have been deleted");

            list = dir.listFiles();
            assert list != null;
            int nbError=0;
            for (File file : list){
                String name = file.getName().split("\\.")[0];
                String mName = getMaterialName(sender,name);
                if (mName==null) nbError++;
                int color = 0;
                try {
                    color = getAverageColor(ImageIO.read(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (nbError>0) sender.sendMessage("§cnbError = "+nbError);
        }
        sender.sendMessage("§2taking into account custom textures");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }

    private static int getAverageColor(BufferedImage img){
        int r=0;
        int g=0;
        int b=0;
        int a=0;
        int nb=0;
        for (int i=0; i<img.getWidth(); i++){
            for (int j=0; j<img.getHeight(); j++){
                Color color = new Color(img.getRGB(i,j),true);
                r+=color.getRed();
                g+=color.getGreen();
                b+=color.getBlue();
                a+=color.getAlpha();
                nb++;
            }
        }
        if (nb==0) nb=1;
        return new Color(r/nb,g/nb,b/nb,a/nb).getRGB();
    }

    private static String getMaterialName(CommandSender sender, String textureName){
        String name = textureName.split("_top")[0];
        name = name.split("_side")[0];
        name = name.split("_bottom")[0];
        name = name.split("_front")[0];
        name = name.split("_down")[0];
        name = name.split("_up")[0];
        name = name.split("_base")[0];
        name = name.split("_lit")[0];
        name = name.split("_stage")[0];
        name = name.split("_overlay")[0];
        name = name.split("_back")[0];
        name = name.split("_on")[0];
        name = name.split("_off")[0];
        name = name.split("_inside")[0];
        name = name.split("_outside")[0];
        name = name.split("_moist")[0];
        name = name.split("_inverted")[0];
        name = name.split("_inner")[0];
        if (name.contains("_pot")){
            if (!name.equals("flower_pot")) name = "potted_" + name.split("_pot")[0];
        }
        for (String s : new String[]{"turtle_egg","structure_block","small_dripleaf","jigsaw","grindstone","frosted_ice","campfire","beehive"}){
            if (name.contains(s)) {name = s; break;}
        }
        if (name.contains("redstone_dust")) name = "redstone_wire";
        if (name.contains("rail_corner")) name = "rail";
        if (name.contains("sticky")) name = "sticky_piston";
        if (name.equals("big_dripleaf_tip")) name = "big_dripleaf";
        else if (name.contains("piston")) name = "piston";
        if (name.equals("mushroom_block")) name = "brown_"+name;
        name = name.toUpperCase(Locale.ROOT);
        Material m = Material.matchMaterial(name);
        if (m==null) {
            sender.sendMessage("§c"+textureName+" alias "+name+" not found correspondance !");
            return null;
        }
        return m.name();
    }
}
