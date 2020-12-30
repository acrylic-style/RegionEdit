package xyz.acrylicstyle.region.internal.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.exception.RegionEditException;
import xyz.acrylicstyle.region.api.region.CuboidRegion;
import xyz.acrylicstyle.region.api.region.RegionSelection;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;

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
            /* these locations isn't null and it's already safe to use, just for suppressing warnings */
            assert region.getLocation() != null;
            assert region.getLocation2() != null;
            RegionEdit.getBlocksInvertAsync(region.getLocation(), region.getLocation2(), Material.AIR, (blocks, throwable) -> {
                RegionEdit.getInstance().getHistoryManager().resetPointer(player.getUniqueId());
                RegionEdit.pool.execute(() -> RegionEditPlugin.setBlocks0(player, blocks, Material.AIR, (byte) 0));
            });
        } else {
            throw new RegionEditException("Invalid RegionSelection class: " + regionSelection.getClass().getCanonicalName());
        }
    }
}
