package xyz.acrylicstyle.region.internal.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.exception.RegionEditException;
import xyz.acrylicstyle.region.api.region.CuboidRegion;
import xyz.acrylicstyle.region.api.region.RegionSelection;
import xyz.acrylicstyle.region.internal.utils.Reflection;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;

import java.util.Map;

public class SetCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        if (!RegionEditPlugin.regionSelection.getOrDefault(player.getUniqueId(), new CuboidRegion(null, null)).isValid()) {
            player.sendMessage(ChatColor.RED + "You must make selection first.");
            return;
        }
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Error: Not enough arguments.");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /" + RegionEditPlugin.COMMAND_PREFIX + "set <block>");
            player.sendMessage(ChatColor.YELLOW + "Set blocks.");
            return;
        }
        Map.Entry<Material, Byte> entry = RegionEdit.getInstance().resolveMaterial(args[0]);
        if (entry == null) {
            player.sendMessage(ChatColor.RED + "Error: Invalid block: " + (args[0] + ":").split(":")[0].toLowerCase());
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /" + RegionEditPlugin.COMMAND_PREFIX + "set <block>");
            player.sendMessage(ChatColor.YELLOW + "Set blocks.");
            return;
        }
        int data = entry.getValue();
        if (data == -1) data = 0;
        Material material = entry.getKey();
        RegionSelection regionSelection = RegionEditPlugin.regionSelection.get(player.getUniqueId());
        if (regionSelection instanceof CuboidRegion) {
            CuboidRegion region = (CuboidRegion) regionSelection;
            assert region.getLocation() != null;
            int finalData = data;
            RegionEdit.getBlocksAsync(region.getLocation(), region.getLocation2(), null, block -> block.getType() != material || Reflection.getData(block) != finalData, (blocks, throwable) -> {
                RegionEdit.getInstance().getHistoryManager().resetPointer(player.getUniqueId());
                RegionEdit.pool.execute(() -> RegionEditPlugin.setBlocks(player, blocks, material, (byte) finalData));
            });
        } else {
            throw new RegionEditException("Invalid RegionSelection class: " + regionSelection.getClass().getCanonicalName());
        }
    }
}
