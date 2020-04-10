package xyz.acrylicstyle.region.internal.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import util.CollectionList;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.internal.utils.Compatibility;
import xyz.acrylicstyle.tomeito_core.command.PlayerCommandExecutor;
import xyz.acrylicstyle.tomeito_core.utils.Callback;
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
        RegionEdit.getNearbyBlocksAsync(player.getLocation(), finalRadius, new Callback<CollectionList<Block>>() {
            @Override
            public void done(CollectionList<Block> blocks, Throwable throwable) {
                blocks = blocks.filter(block -> block.getType() == (finalLava ? Material.LAVA : Material.WATER) || (Compatibility.checkStationary_Water() && block.getType() == (finalLava ? Material.STATIONARY_LAVA : Material.STATIONARY_WATER)));
                RegionEdit.getInstance().getHistoryManager().resetPointer(player.getUniqueId());
                RegionEditPlugin.setBlocks(player, blocks, Material.AIR, (byte) 0);
            }
        });
    }
}
