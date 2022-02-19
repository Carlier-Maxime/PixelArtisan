package fr.metouais.pixelartisan.commands;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CreateCommand extends MyCommand{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player player){
            if (player.isOp()){
                Block block = player.getLocation().getBlock();
                block.setType(Material.ACACIA_LOG);
                return true;
            } else {
                sender.sendMessage("§cPermission denied for this operation !");
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }
}
