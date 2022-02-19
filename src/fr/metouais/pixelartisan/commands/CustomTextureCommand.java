package fr.metouais.pixelartisan.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

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
            for (File file : list){
                if (!file.isFile()) file.delete();
                String[] nameSplit = file.getName().split("\\.");
                if (nameSplit[nameSplit.length-1].equals("mcmeta")){
                    file.delete();
                    new File(pathCustomTexture+"/"+nameSplit[0]+".png").delete();
                } else if (!nameSplit[nameSplit.length-1].equals("png")) file.delete();
            }
            list = dir.listFiles();
            assert list != null;
            for (File file : list){
                String name = file.getName().split("\\.")[0];
                int color = 0;
                try {
                    color = getAverageColor(ImageIO.read(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
}
