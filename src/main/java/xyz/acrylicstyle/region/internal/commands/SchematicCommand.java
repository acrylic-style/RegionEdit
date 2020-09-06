package xyz.acrylicstyle.region.internal.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import util.ICollectionList;
import util.Watchdog;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.block.state.BlockState;
import xyz.acrylicstyle.region.api.exception.RegionEditException;
import xyz.acrylicstyle.region.api.schematic.Schematic;
import xyz.acrylicstyle.region.api.schematic.SchematicManager;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;
import xyz.acrylicstyle.tomeito_api.utils.Log;

import java.io.File;

public class SchematicCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Error: Not enough arguments.");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /" + RegionEditPlugin.COMMAND_PREFIX + "schem <load|list>");
            return;
        }
        if (args[0].equals("load")) {
            if (args.length == 1) {
                player.sendMessage(ChatColor.RED + "Error: Not enough arguments.");
                player.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /" + RegionEditPlugin.COMMAND_PREFIX + "schem load <filename>");
                return;
            }
            double start = RegionEdit.memoryUsageInGBRounded();
            new Watchdog("Schematic->load@" + player.getName(), () -> {
                player.sendMessage(ChatColor.LIGHT_PURPLE + "Loading schematic...");
                Schematic schematic = SchematicManager.load(new File("./plugins/RegionEdit/schematics/" + args[1] + ".schem"));
                if (schematic == null) {
                    player.sendMessage(ChatColor.RED + "Schematic " + ChatColor.YELLOW + args[1] + ".schem " + ChatColor.RED + "doesn't exist.");
                    return;
                }
                @NotNull ICollectionList<BlockState> blocks = schematic.getBlocks();
                RegionEdit.getInstance().getUserSession(player).setClipboard(blocks);
                double end = RegionEdit.memoryUsageInGBRounded();
                player.sendMessage(ChatColor.GREEN + "Loaded schematic " + ChatColor.YELLOW + args[1] + ChatColor.GREEN + ". Run " + ChatColor.GOLD + "//paste " + ChatColor.GREEN + "to paste it.");
                Log.as("RegionEdit").info("Memory usage: start: " + start + ", end: " + end);
            }, 60000, () -> player.sendMessage(ChatColor.RED + "Loading of the schematic was timed out. (took 60 seconds)")).start();
        } else if (args[0].equals("list")) {
            throw new RegionEditException("Not implemented.");
        } else {
            player.sendMessage(ChatColor.RED + "Error: Unknown command " + ChatColor.YELLOW + args[0] + ChatColor.RED + ".");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /" + RegionEditPlugin.COMMAND_PREFIX + "schem <load|list>");
        }
    }
}
