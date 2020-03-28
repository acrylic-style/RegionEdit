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
import xyz.acrylicstyle.region.api.exception.RegionSelectorException;
import xyz.acrylicstyle.region.api.region.CuboidRegion;
import xyz.acrylicstyle.region.api.region.RegionSelection;
import xyz.acrylicstyle.tomeito_core.command.PlayerCommandExecutor;

import java.util.function.Function;

public class SetCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        if (!RegionEditPlugin.regionSelection.getOrDefault(player.getUniqueId(), new CuboidRegion(null, null)).isValid()) {
            player.sendMessage(ChatColor.RED + "You must make selection first.");
            return;
        }
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Error: Not enough arguments.");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /set <block>");
            player.sendMessage(ChatColor.YELLOW + "Set blocks.");
            return;
        }
        CollectionList<String> materials = ICollectionList.asList(Material.values()).filter(Material::isBlock).map(Enum::name).map((Function<String, String>) String::toLowerCase);
        if (!materials.contains((args[0] + ":").split(":")[0].toLowerCase())) {
            player.sendMessage(ChatColor.RED + "Error: Invalid block: " + (args[0] + ":").split(":")[0].toLowerCase());
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /set <block>");
            player.sendMessage(ChatColor.YELLOW + "Set blocks.");
            return;
        }
        int data = JavaScript.parseInt((args[0] + ":0").split(":")[1], 10);
        Material material = Material.getMaterial(materials.filter(s -> s.equalsIgnoreCase(args[0])).first().toUpperCase());
        RegionSelection regionSelection = RegionEditPlugin.regionSelection.get(player.getUniqueId());
        if (regionSelection instanceof CuboidRegion) {
            CuboidRegion region = (CuboidRegion) regionSelection;
            player.sendMessage(ChatColor.GREEN + "Fetching blocks...");
            new BukkitRunnable() {
                @Override
                public void run() {
                    CollectionList<Block> blocks = RegionEdit.getBlocks(region.getLocation(), region.getLocation2(), null, null);
                    RegionEdit.getInstance().getHistoryManager().resetPointer(player.getUniqueId());
                    RegionEditPlugin.setBlocks(player, blocks, material, (byte) data);
                }
            }.runTaskAsynchronously(RegionEdit.getInstance());
        } else {
            throw new RegionSelectorException("Invalid RegionSelection class: " + regionSelection.getClass().getCanonicalName());
        }
    }
}
