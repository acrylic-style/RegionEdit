package xyz.acrylicstyle.region.internal.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.selection.SelectionMode;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;

public class WandCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        player.getInventory().addItem(new ItemStack(RegionEdit.getInstance().getWandItem()));
        SelectionMode mode = RegionEdit.getInstance().getUserSession(player).getSelectionMode();
        if (mode == SelectionMode.CUBOID) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Left click: #1 pos");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Right click: #2 pos");
        }
    }
}
