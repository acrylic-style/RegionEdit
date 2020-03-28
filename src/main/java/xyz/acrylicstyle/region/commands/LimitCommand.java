package xyz.acrylicstyle.region.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.tomeito_core.command.PlayerCommandExecutor;
import xyz.acrylicstyle.tomeito_core.utils.TypeUtil;

public class LimitCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        if (args.length == 0 || !TypeUtil.isInt(args[0])) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /limit <number>");
            player.sendMessage(ChatColor.YELLOW + "Limits blocks per ticks.");
            player.sendMessage(ChatColor.GREEN + "Current limit is " + ChatColor.RED + RegionEditPlugin.blocksPerTick + ChatColor.GREEN + ".");
            return;
        }
        int blocks = Integer.parseInt(args[0]);
        if (blocks <= 0) {
            player.sendMessage(ChatColor.RED + "Error: Number cannot be lower than 1.");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /limit <number>");
            player.sendMessage(ChatColor.YELLOW + "Limits blocks per ticks.");
            return;
        }
        RegionEditPlugin.blocksPerTick = blocks;
        player.sendMessage(ChatColor.GREEN + "Successfully set blocks per ticks to " + ChatColor.RED + blocks + ChatColor.GREEN + ".");
        if (blocks < 16) player.sendMessage(ChatColor.YELLOW + "Note: " + blocks + " blocks per tick is not recommended and might be slow.");
    }
}
