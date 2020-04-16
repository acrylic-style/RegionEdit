package xyz.acrylicstyle.region.internal.commands;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import util.CollectionList;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.region.CuboidRegion;
import xyz.acrylicstyle.region.api.selection.SelectionMode;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;

public class ChunkCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        SelectionMode selectionMode = RegionEditPlugin.selectionMode.getOrDefault(player.getUniqueId(), SelectionMode.CUBOID);
        if (selectionMode == SelectionMode.CUBOID) {
            World world = player.getWorld();
            Chunk chunk = player.getLocation().getChunk();
            int X = chunk.getX() * 16;
            int Z = chunk.getZ() * 16;
            CuboidRegion reg = new CuboidRegion(new Location(world, X, 0, Z), new Location(world, X+15, 255, Z+15));
            RegionEditPlugin.regionSelection.add(player.getUniqueId(), reg);
            assert reg.getLocation() != null;
            CollectionList<Block> blocks = RegionEdit.getBlocks(reg.getLocation(), reg.getLocation2(), null, null);
            player.sendMessage(ChatColor.GREEN + "Selected region "
                    + ChatColor.YELLOW + "(" + loc2Str(reg.getLocation()) + " -> " + loc2Str(reg.getLocation2()) + ") "
                    + ChatColor.LIGHT_PURPLE + "(" + blocks.size() + " blocks)");
        } else player.sendMessage(ChatColor.RED + "This command cannot be used when you're not in the cuboid selection mode.");
    }

    private String loc2Str(Location location) {
        if (location == null) return "null";
        return String.format(ChatColor.LIGHT_PURPLE + "%d, %d, %d" + ChatColor.YELLOW, location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}
