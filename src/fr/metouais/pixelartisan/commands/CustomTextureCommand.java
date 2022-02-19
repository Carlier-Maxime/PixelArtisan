package fr.metouais.pixelartisan.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
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
        }
        sender.sendMessage("§2taking into account custom textures");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }
}
