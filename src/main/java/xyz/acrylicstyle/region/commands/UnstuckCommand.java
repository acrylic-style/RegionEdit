package xyz.acrylicstyle.region.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.acrylicstyle.tomeito_core.command.PlayerCommandExecutor;

public class UnstuckCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] strings) {
        int i = 0;
        while (player.getLocation().add(0, i+1, 0).getBlock().getType() != Material.AIR) i++;
        Location location = player.getLocation().clone().add(0.5, i+1, 0.5);
        player.teleport(location);
        player.sendMessage(ChatColor.GREEN + "You are no longer stuck.");
    }
}
