package xyz.acrylicstyle.region.internal.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.player.UserSession;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;

public class DrawSelCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        UserSession session = RegionEdit.getInstance().getUserSession(player);
        if (session.isDrawSelection()) {
            player.sendMessage(ChatColor.GREEN + "Turned on drawsel mode.");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "This mode allows you to highlight selected");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "cuboid area without having to install mods.");
            session.setDrawSelection(false);
        } else {
            player.sendMessage(ChatColor.GREEN + "Turned off drawsel mode.");
            session.setDrawSelection(true);
        }
    }
}
