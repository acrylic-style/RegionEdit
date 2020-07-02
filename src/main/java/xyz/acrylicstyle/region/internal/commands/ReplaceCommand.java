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

public class ReplaceCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        if (!RegionEditPlugin.regionSelection.getOrDefault(player.getUniqueId(), new CuboidRegion(null, null)).isValid()) {
            player.sendMessage(ChatColor.RED + "You must make selection first.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Error: Not enough arguments.");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /replace <before> <after>");
            player.sendMessage(ChatColor.YELLOW + "Replace blocks.");
            return;
        }
        Map.Entry<Material, Byte> entry1 = RegionEdit.getInstance().resolveMaterial(args[0]);
        Map.Entry<Material, Byte> entry2 = RegionEdit.getInstance().resolveMaterial(args[1]);
        if (entry1 == null) {
            player.sendMessage(ChatColor.RED + "Error: Invalid block #1: " + args[0].toLowerCase());
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /replace <before> <after>");
            player.sendMessage(ChatColor.YELLOW + "Replace blocks.");
            return;
        }
        if (entry2 == null) {
            player.sendMessage(ChatColor.RED + "Error: Invalid block #2: " + args[1].toLowerCase());
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /replace <before> <after>");
            player.sendMessage(ChatColor.YELLOW + "Replace blocks.");
            return;
        }
        int data1 = entry1.getValue();
        int data2 = entry2.getValue();
        Material material1 = entry1.getKey();
        Material material2 = entry2.getKey();
        RegionSelection regionSelection = RegionEditPlugin.regionSelection.get(player.getUniqueId());
        if (regionSelection instanceof CuboidRegion) {
            CuboidRegion region = (CuboidRegion) regionSelection;
            assert region.getLocation() != null;
            if (args[0].startsWith("!")) {
                RegionEdit.getBlocksInvertAsync(region.getLocation(), region.getLocation2(), material1, (blocks, throwable) -> {
                    blocks = blocks.filter(block -> Reflection.getData(block) != (byte) data1);
                    RegionEdit.getInstance().getHistoryManager().resetPointer(player.getUniqueId());
                    RegionEditPlugin.setBlocks(player, blocks, material2, (byte) data2);
                });
            } else {
                RegionEdit.getBlocksAsync(region.getLocation(), region.getLocation2(), material1, block -> Reflection.getData(block) == (byte) data1, (blocks, throwable) -> {
                    RegionEdit.getInstance().getHistoryManager().resetPointer(player.getUniqueId());
                    RegionEditPlugin.setBlocks(player, blocks, material2, (byte) data2);
                });
            }
        } else {
            throw new RegionEditException("Invalid RegionSelection class: " + regionSelection.getClass().getCanonicalName());
        }
    }
}
