package fr.metouais.pixelartisan.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class PixelArtisanCommand extends MyCommand {
    MyCommand subCmd;

    public PixelArtisanCommand() {
        subCmd = null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.isOp()){
            updateSubCmd(args);
            if (subCmd!=null) return subCmd.onCommand(sender, cmd, label, Arrays.copyOfRange(args, 1, args.length));
            else {
                sender.sendMessage("§csubCommand not exists !");
                sender.sendMessage("§c/"+label+" [create|customTexture]");
            }
        } else {
            sender.sendMessage("§cyou must be op");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        updateSubCmd(args);
        if (subCmd!=null) return subCmd.onTabComplete(sender, cmd, label, Arrays.copyOfRange(args, 1, args.length));
        else if (args.length>1) return null;
        return List.of(new String[]{
                "create",
                "customTexture"
        });
    }

    private void updateSubCmd(String[] args){
        if (args.length<=0) {subCmd=null; return;}
        switch (args[0]){
            case "create" -> subCmd=new CreateCommand();
            case "customTexture" -> subCmd=new CustomTextureCommand();
            default -> subCmd=null;
        }
    }
}
