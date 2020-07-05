package xyz.acrylicstyle.region.internal.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.selection.SelectionMode;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;

public class SelectionCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        if (args.length == 0) {
            RegionEditPlugin.regionSelection.remove(player.getUniqueId());
            RegionEditPlugin.sessions.get(player.getUniqueId()).sendCUIEvent();
            player.sendMessage(ChatColor.GREEN + "Selection cleared.");
            return;
        }
        if (args[0].equalsIgnoreCase("cuboid")) {
            RegionEdit.getInstance().getUserSession(player).setSelectionMode(SelectionMode.CUBOID);
            player.sendMessage(ChatColor.GREEN + "Switched to the " + ChatColor.YELLOW + "cuboid " + ChatColor.GREEN + "mode!");
            player.sendMessage(ChatColor.YELLOW + "Left click: Position #1");
            player.sendMessage(ChatColor.YELLOW + "Right click: Position #2");
        } else {
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /sel [cuboid]");
            player.sendMessage(ChatColor.YELLOW + "Clears the selection or switch to the other selection mode.");
        }
    }
}
