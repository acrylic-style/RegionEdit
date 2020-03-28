package xyz.acrylicstyle.region.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import util.CollectionList;
import util.ICollectionList;
import util.javascript.JavaScript;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.exception.RegionEditException;
import xyz.acrylicstyle.region.api.region.CuboidRegion;
import xyz.acrylicstyle.region.api.region.RegionSelection;
import xyz.acrylicstyle.tomeito_core.command.PlayerCommandExecutor;

import java.util.function.Function;

public class ReplaceCommand extends PlayerCommandExecutor {
    @SuppressWarnings("deprecation")
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
        CollectionList<String> materials = ICollectionList.asList(Material.values()).filter(Material::isBlock).map(Enum::name).map((Function<String, String>) String::toLowerCase);
        if (!materials.contains((args[0] + ":").split(":")[0].toLowerCase())) {
            player.sendMessage(ChatColor.RED + "Error: Invalid block: " + (args[0] + ":").split(":")[0].toLowerCase());
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /replace <before> <after>");
            player.sendMessage(ChatColor.YELLOW + "Replace blocks.");
            return;
        }
        if (!materials.contains((args[1] + ":").split(":")[0].toLowerCase())) {
            player.sendMessage(ChatColor.RED + "Error: Invalid block: " + (args[1] + ":").split(":")[0].toLowerCase());
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /replace <before> <after>");
            player.sendMessage(ChatColor.YELLOW + "Replace blocks.");
            return;
        }
        int beforeData = JavaScript.parseInt((args[0] + ":0").split(":")[1], 10);
        int afterData = JavaScript.parseInt((args[1] + ":0").split(":")[1], 10);
        Material before = Material.getMaterial(materials.filter(s -> s.equalsIgnoreCase(args[0])).first().toUpperCase());
        Material after = Material.getMaterial(materials.filter(s -> s.equalsIgnoreCase(args[1])).first().toUpperCase());
        RegionSelection regionSelection = RegionEditPlugin.regionSelection.get(player.getUniqueId());
        if (regionSelection instanceof CuboidRegion) {
            CuboidRegion region = (CuboidRegion) regionSelection;
            new BukkitRunnable() {
                @Override
                public void run() {
                    CollectionList<Block> blocks;
                    if (args[0].startsWith("!")) {
                        blocks = RegionEdit.getBlocksInvert(region.getLocation(), region.getLocation2(), before);
                    } else {
                        blocks = RegionEdit.getBlocks(region.getLocation(), region.getLocation2(), before, block -> block.getData() == (byte) beforeData);
                    }
                    RegionEdit.getInstance().getHistoryManager().resetPointer(player.getUniqueId());
                    RegionEditPlugin.setBlocks(player, blocks, after, (byte) afterData);
                }
            }.runTaskAsynchronously(RegionEdit.getInstance());
        } else {
            throw new RegionEditException("Invalid RegionSelection class: " + regionSelection.getClass().getCanonicalName());
        }
    }
}
