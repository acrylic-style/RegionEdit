package xyz.acrylicstyle.region.internal.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import util.CollectionList;
import util.ICollectionList;
import util.javascript.JavaScript;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.exception.RegionEditException;
import xyz.acrylicstyle.region.api.region.CuboidRegion;
import xyz.acrylicstyle.region.api.region.RegionSelection;
import xyz.acrylicstyle.region.internal.block.Blocks;
import xyz.acrylicstyle.region.internal.utils.Reflection;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;

import java.util.Objects;
import java.util.function.Function;

public class SetCommand extends PlayerCommandExecutor {
    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void onCommand(Player player, String[] args) {
        if (!RegionEditPlugin.regionSelection.getOrDefault(player.getUniqueId(), new CuboidRegion(null, null)).isValid()) {
            player.sendMessage(ChatColor.RED + "You must make selection first.");
            return;
        }
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Error: Not enough arguments.");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: //set <block>");
            player.sendMessage(ChatColor.YELLOW + "Set blocks.");
            return;
        }
        int data;
        Material material;
        if (args[0].matches("^\\d+|\\d+:\\d+$")) {
            String[] arr = args[0].split(":");
            if (arr.length != 1) {
                data = Integer.parseInt(arr[1]);
            } else {
                data = 0;
            }
            material = Blocks.getMaterialById(Integer.parseInt(args[0]));
        } else {
            CollectionList<String> materials = ICollectionList.asList(Material.values()).filter(Material::isBlock).map(Enum::name).map((Function<String, String>) String::toLowerCase);
            if (!materials.contains((args[0]).split(":")[0].toLowerCase())) {
                player.sendMessage(ChatColor.RED + "Error: Invalid block: " + (args[0] + ":").split(":")[0].toLowerCase());
                player.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: //set <block>");
                player.sendMessage(ChatColor.YELLOW + "Set blocks.");
                return;
            }
            data = JavaScript.parseInt((args[0] + ":0").split(":")[1], 10);
            material = Material.getMaterial(Objects.requireNonNull(materials.filter(s -> s.equalsIgnoreCase((args[0] + ":0").split(":")[0])).first()).toUpperCase());
        }
        RegionSelection regionSelection = RegionEditPlugin.regionSelection.get(player.getUniqueId());
        if (regionSelection instanceof CuboidRegion) {
            CuboidRegion region = (CuboidRegion) regionSelection;
            assert region.getLocation() != null;
            RegionEdit.getBlocksAsync(region.getLocation(), region.getLocation2(), null, block -> block.getType() != material || Reflection.getData(block) != data, (blocks, throwable) -> {
                RegionEdit.getInstance().getHistoryManager().resetPointer(player.getUniqueId());
                RegionEditPlugin.setBlocks(player, blocks, material, (byte) data);
            });
        } else {
            throw new RegionEditException("Invalid RegionSelection class: " + regionSelection.getClass().getCanonicalName());
        }
    }
}
