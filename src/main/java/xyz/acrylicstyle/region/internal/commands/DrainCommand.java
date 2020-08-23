package xyz.acrylicstyle.region.internal.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import util.CollectionList;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.internal.utils.BukkitVersion;
import xyz.acrylicstyle.region.internal.utils.Compatibility;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;
import xyz.acrylicstyle.tomeito_api.utils.TypeUtil;

public class DrainCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        int radius = 10;
        BlockType type = BlockType.WATER;
        if (args.length != 0) {
            if (!TypeUtil.isInt(args[0])) {
                player.sendMessage(ChatColor.LIGHT_PURPLE + "/" + RegionEditPlugin.COMMAND_PREFIX + "drain [radius: 10] [lava/kelp/seagrass]");
                player.sendMessage(ChatColor.YELLOW + "Drains water/lava near you.");
                return;
            }
            radius = Integer.parseInt(args[0]);
            if (radius <= 0) {
                player.sendMessage(ChatColor.RED + "Invalid radius: " + radius);
                return;
            }
            if (args.length >= 2) {
                if (args[1].equalsIgnoreCase("lava")) type = BlockType.LAVA;
                if (args[1].equalsIgnoreCase("kelp")) {
                    if (!Compatibility.getBukkitVersion().atLeast(BukkitVersion.v1_13)) {
                        player.sendMessage(ChatColor.RED + "This server doesn't support this block!");
                        return;
                    }
                    type = BlockType.KELP;
                }
                if (args[1].equalsIgnoreCase("seagrass")) {
                    if (!Compatibility.getBukkitVersion().atLeast(BukkitVersion.v1_13)) {
                        player.sendMessage(ChatColor.RED + "This server doesn't support this block!");
                        return;
                    }
                    type = BlockType.SEAGRASS;
                }
            }
        }
        int finalRadius = radius;
        final BlockType finalType = type;
        player.sendMessage(ChatColor.GREEN + "Fetching blocks...");
        RegionEdit.pool.execute(() -> {
            CollectionList<Block> blocks = RegionEdit.getNearbyBlocks(player.getLocation(), finalRadius, block -> {
                if (finalType == BlockType.LAVA) {
                    return block.getType() == Material.LAVA || (Compatibility.checkStationary_Water() && block.getType() == Material.STATIONARY_LAVA);
                } else if (finalType == BlockType.WATER) {
                    return block.getType() == Material.WATER || (Compatibility.checkStationary_Water() && block.getType() == Material.STATIONARY_WATER);
                } else if (finalType == BlockType.KELP) {
                    return block.getType() == Material.getMaterial("KELP") || block.getType() == Material.getMaterial("KELP_PLANT");
                } else {
                    return block.getType() == Material.getMaterial("SEAGRASS") || block.getType() == Material.getMaterial("TALL_SEAGRASS");
                }
            });
            RegionEditPlugin.setBlocks(player, blocks, Material.AIR, (byte) 0);
        });
    }

    private enum BlockType {
        KELP,
        LAVA,
        WATER,
        SEAGRASS,
    }
}
