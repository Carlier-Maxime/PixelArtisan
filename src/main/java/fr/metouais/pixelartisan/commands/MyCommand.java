package fr.metouais.pixelartisan.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class MyCommand implements CommandExecutor, TabCompleter {
    @Override
    public abstract boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args);

    @Override
    public abstract List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args);
}
