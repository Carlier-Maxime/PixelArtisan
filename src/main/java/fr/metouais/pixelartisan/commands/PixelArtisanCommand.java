package fr.metouais.pixelartisan.commands;

import fr.metouais.pixelartisan.Utils.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
                if (args.length>0 && Objects.equals(args[0], "debug")){
                    ChatUtils.sendMessage(sender,"§bdebug for the dev");
                } else {
                    ChatUtils.sendMessage(sender,"§csubCommand not exists !");
                    ChatUtils.sendMessage(sender,"§c/"+label+" [create|customTexture]");
                }
            }
        } else {
            ChatUtils.sendMessage(sender,"§cyou must be op");
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
                "customTexture",
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
