package xyz.acrylicstyle.region.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import util.CollectionList;
import util.ICollectionList;
import util.javascript.JavaScript;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.exception.RegionEditException;
import xyz.acrylicstyle.region.api.region.CuboidRegion;
import xyz.acrylicstyle.region.api.region.RegionSelection;
import xyz.acrylicstyle.region.internal.utils.Reflection;
import xyz.acrylicstyle.tomeito_core.command.PlayerCommandExecutor;
import xyz.acrylicstyle.tomeito_core.utils.Callback;

import java.util.function.Function;

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
        String beforeMaterial = (args[0].replaceFirst("!", "") + ":").split(":")[0].toUpperCase();
        String afterMaterial = (args[1].replaceFirst("!", "") + ":").split(":")[0].toUpperCase();
        CollectionList<String> materials = ICollectionList.asList(Material.values()).filter(Material::isBlock).map(Enum::name).map((Function<String, String>) String::toLowerCase);
        if (!materials.contains(beforeMaterial.toLowerCase())) {
            player.sendMessage(ChatColor.RED + "Error: Invalid block: " + beforeMaterial.toLowerCase());
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /replace <before> <after>");
            player.sendMessage(ChatColor.YELLOW + "Replace blocks.");
            return;
        }
        if (!materials.contains(afterMaterial.toLowerCase())) {
            player.sendMessage(ChatColor.RED + "Error: Invalid block: " + afterMaterial.toLowerCase());
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /replace <before> <after>");
            player.sendMessage(ChatColor.YELLOW + "Replace blocks.");
            return;
        }
        int beforeData = JavaScript.parseInt((args[0] + ":0").split(":")[1], 10);
        int afterData = JavaScript.parseInt((args[1] + ":0").split(":")[1], 10);
        Material before = Material.getMaterial(materials.filter(s -> s.equalsIgnoreCase(beforeMaterial)).first().toUpperCase());
        Material after = Material.getMaterial(materials.filter(s -> s.equalsIgnoreCase(afterMaterial)).first().toUpperCase());
        RegionSelection regionSelection = RegionEditPlugin.regionSelection.get(player.getUniqueId());
        if (regionSelection instanceof CuboidRegion) {
            CuboidRegion region = (CuboidRegion) regionSelection;
            if (args[0].startsWith("!")) {
                RegionEdit.getBlocksInvertAsync(region.getLocation(), region.getLocation2(), before, new Callback<CollectionList<Block>>() {
                    @Override
                    public void done(CollectionList<Block> blocks, Throwable throwable) {
                        RegionEdit.getInstance().getHistoryManager().resetPointer(player.getUniqueId());
                        RegionEditPlugin.setBlocks(player, blocks, after, (byte) afterData);
                    }
                });
            } else {
                RegionEdit.getBlocksAsync(region.getLocation(), region.getLocation2(), before, block -> Reflection.getData(block) == (byte) beforeData, new Callback<CollectionList<Block>>() {
                    @Override
                    public void done(CollectionList<Block> blocks, Throwable throwable) {
                        RegionEdit.getInstance().getHistoryManager().resetPointer(player.getUniqueId());
                        RegionEditPlugin.setBlocks(player, blocks, after, (byte) afterData);
                    }
                });
            }
        } else {
            throw new RegionEditException("Invalid RegionSelection class: " + regionSelection.getClass().getCanonicalName());
        }
    }
}
