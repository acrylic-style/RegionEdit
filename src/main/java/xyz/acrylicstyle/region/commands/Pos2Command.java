package xyz.acrylicstyle.region.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import util.CollectionList;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.region.CuboidRegion;
import xyz.acrylicstyle.region.api.selection.SelectionMode;
import xyz.acrylicstyle.tomeito_core.command.PlayerCommandExecutor;

public class Pos2Command extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] strings) {
        SelectionMode selectionMode = RegionEditPlugin.selectionMode.getOrDefault(player.getUniqueId(), SelectionMode.CUBOID);
        if (selectionMode == SelectionMode.CUBOID) {
            CuboidRegion cuboidRegion = (CuboidRegion) RegionEditPlugin.regionSelection.getOrDefault(player.getUniqueId(), new CuboidRegion(player.getLocation(), player.getLocation()));
            if (!cuboidRegion.getLocation().getWorld().equals(player.getWorld()))
                cuboidRegion = new CuboidRegion(player.getLocation(), player.getLocation());
            CuboidRegion reg = new CuboidRegion(cuboidRegion.getLocation(), player.getLocation());
            RegionEditPlugin.regionSelection.add(player.getUniqueId(), reg);
            CollectionList<Block> blocks = RegionEdit.getBlocks(reg.getLocation(), reg.getLocation2(), null, null);
            player.sendMessage(ChatColor.GREEN + "Selected region "
                    + ChatColor.YELLOW + "(" + loc2Str(reg.getLocation()) + " -> " + loc2Str(reg.getLocation2()) + ") "
                    + ChatColor.LIGHT_PURPLE + "(" + blocks.size() + " blocks)");
        }
    }

    private String loc2Str(Location location) {
        if (location == null) return "null";
        return String.format(ChatColor.LIGHT_PURPLE + "%d, %d, %d" + ChatColor.YELLOW, location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}
