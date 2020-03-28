package xyz.acrylicstyle.region.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import util.CollectionList;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.exception.RegionEditException;
import xyz.acrylicstyle.region.api.region.CuboidRegion;
import xyz.acrylicstyle.region.api.region.RegionSelection;
import xyz.acrylicstyle.tomeito_core.command.PlayerCommandExecutor;

public class CutCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        if (!RegionEditPlugin.regionSelection.getOrDefault(player.getUniqueId(), new CuboidRegion(null, null)).isValid()) {
            player.sendMessage(ChatColor.RED + "You must make selection first.");
            return;
        }
        RegionSelection regionSelection = RegionEditPlugin.regionSelection.get(player.getUniqueId());
        if (regionSelection instanceof CuboidRegion) {
            CuboidRegion region = (CuboidRegion) regionSelection;
            new BukkitRunnable() {
                @Override
                public void run() {
                    CollectionList<Block> blocks = RegionEdit.getBlocksInvert(region.getLocation(), region.getLocation2(), Material.AIR);
                    RegionEdit.getInstance().getHistoryManager().resetPointer(player.getUniqueId());
                    RegionEditPlugin.setBlocks(player, blocks, Material.AIR, (byte) 0);
                }
            }.runTaskAsynchronously(RegionEdit.getInstance());
        } else {
            throw new RegionEditException("Invalid RegionSelection class: " + regionSelection.getClass().getCanonicalName());
        }
    }
}
