package xyz.acrylicstyle.region.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.tomeito_core.command.PlayerCommandExecutor;

public class FastCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        boolean f = !RegionEdit.getInstance().getUserSession(player).isFastMode();
        RegionEdit.getInstance().getUserSession(player).setFastMode(f);
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Turned " + (f ? "on" : "off") + " the Fast Mode.");
    }
}
