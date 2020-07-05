package xyz.acrylicstyle.region.internal.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import util.CollectionList;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.region.CuboidRegion;
import xyz.acrylicstyle.region.api.selection.SelectionMode;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;

import java.util.Set;

public class HPos2Command extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        SelectionMode selectionMode = RegionEditPlugin.selectionMode.getOrDefault(player.getUniqueId(), SelectionMode.CUBOID);
        Block targetBlock = player.getTargetBlock((Set<Material>) null, 100);
        if (targetBlock == null || targetBlock.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "Couldn't find target block! (or too far)");
            return;
        }
        Location location = targetBlock.getLocation();
        if (selectionMode == SelectionMode.CUBOID) {
            CuboidRegion cuboidRegion = (CuboidRegion) RegionEditPlugin.regionSelection.getOrDefault(player.getUniqueId(), new CuboidRegion(location, location));
            assert cuboidRegion.getLocation() != null;
            if (!cuboidRegion.getLocation().getWorld().equals(player.getWorld()))
                cuboidRegion = new CuboidRegion(location, location);
            CuboidRegion reg = new CuboidRegion(cuboidRegion.getLocation(), location);
            RegionEditPlugin.regionSelection.add(player.getUniqueId(), reg);
            RegionEditPlugin.sessions.get(player.getUniqueId()).sendCUIEvent();
            assert reg.getLocation() != null;
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
