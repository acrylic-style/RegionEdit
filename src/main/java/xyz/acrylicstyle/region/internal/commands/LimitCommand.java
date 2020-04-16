package xyz.acrylicstyle.region.internal.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.tomeito_api.utils.TypeUtil;

public class LimitCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender player, Command command, String label, String[] args) {
        if (args.length == 0 || !TypeUtil.isInt(args[0])) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /limit <number>");
            player.sendMessage(ChatColor.YELLOW + "Limits blocks per ticks.");
            player.sendMessage(ChatColor.GREEN + "Current limit is " + ChatColor.RED + RegionEditPlugin.blocksPerTick + ChatColor.GREEN + ".");
            return true;
        }
        int blocks = Integer.parseInt(args[0]);
        if (blocks <= 0) {
            player.sendMessage(ChatColor.RED + "Error: Number cannot be lower than 1.");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /limit <number>");
            player.sendMessage(ChatColor.YELLOW + "Limits blocks per ticks.");
            return true;
        }
        RegionEditPlugin.blocksPerTick = blocks;
        player.sendMessage(ChatColor.GREEN + "Successfully set blocks per ticks to " + ChatColor.RED + blocks + ChatColor.GREEN + ".");
        if (blocks < 16) player.sendMessage(ChatColor.YELLOW + "Note: " + blocks + " blocks per tick is not recommended and might be slow.");
        return true;
    }
}
