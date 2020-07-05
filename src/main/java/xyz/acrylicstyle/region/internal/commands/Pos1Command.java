package xyz.acrylicstyle.region.internal.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import util.CollectionList;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.region.CuboidRegion;
import xyz.acrylicstyle.region.api.selection.SelectionMode;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;

public class Pos1Command extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        SelectionMode selectionMode = RegionEditPlugin.selectionMode.getOrDefault(player.getUniqueId(), SelectionMode.CUBOID);
        if (selectionMode == SelectionMode.CUBOID) {
            Location loc = player.getLocation().getBlock().getLocation();
            CuboidRegion cuboidRegion = (CuboidRegion) RegionEditPlugin.regionSelection.getOrDefault(player.getUniqueId(), new CuboidRegion(loc, loc));
            if (!cuboidRegion.getLocation2().getWorld().equals(player.getWorld())) cuboidRegion = new CuboidRegion(loc, null);
            CuboidRegion reg = new CuboidRegion(loc, cuboidRegion.getLocation2());
            RegionEditPlugin.regionSelection.add(player.getUniqueId(), reg);
            RegionEditPlugin.sessions.get(player.getUniqueId()).sendCUIEvent();
            assert reg.getLocation() != null;
            player.sendMessage(ChatColor.GREEN + "Selected region "
                    + ChatColor.YELLOW + "(" + loc2Str(reg.getLocation()) + " -> " + loc2Str(reg.getLocation2()) + ") "
                    + ChatColor.LIGHT_PURPLE + "(" + reg.size() + " blocks)");
        }
    }

    private String loc2Str(Location location) {
        if (location == null) return "null";
        return String.format(ChatColor.LIGHT_PURPLE + "%d, %d, %d" + ChatColor.YELLOW, location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}
