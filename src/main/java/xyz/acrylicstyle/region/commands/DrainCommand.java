package xyz.acrylicstyle.region.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import util.CollectionList;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.tomeito_core.command.PlayerCommandExecutor;
import xyz.acrylicstyle.tomeito_core.utils.TypeUtil;

public class DrainCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        int radius = 10;
        boolean lava = false;
        if (args.length != 0) {
            if (!TypeUtil.isInt(args[0])) {
                player.sendMessage(ChatColor.LIGHT_PURPLE + "//drain [radius: 10] [lava]");
                player.sendMessage(ChatColor.YELLOW + "Drains water/lava near you.");
                return;
            }
            radius = Integer.parseInt(args[0]);
            if (args.length >= 2) lava = args[1].equalsIgnoreCase("lava");
        }
        int finalRadius = radius;
        boolean finalLava = lava;
        new BukkitRunnable() {
            @Override
            public void run() {
                CollectionList<Block> blocks =
                        RegionEdit.getNearbyBlocks(player.getLocation(), finalRadius)
                                .filter(block -> block.getType() == (finalLava ? Material.LAVA : Material.WATER)
                                        || block.getType() == (finalLava ? Material.STATIONARY_LAVA : Material.STATIONARY_WATER));
                RegionEdit.getInstance().getHistoryManager().resetPointer(player.getUniqueId());
                RegionEditPlugin.setBlocks(player, blocks, Material.AIR, (byte) 0);
            }
        }.runTaskAsynchronously(RegionEdit.getInstance());
    }
}
