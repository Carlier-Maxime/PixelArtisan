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
            Block block = player.getLocation().getBlock();
            block.setType(Material.valueOf("AMETHYST_BLOCK"));
            sender.sendMessage("§2"+Material.AMETHYST_BLOCK.name()+" placed");
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }
}
