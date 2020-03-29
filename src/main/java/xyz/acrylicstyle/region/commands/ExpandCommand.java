package xyz.acrylicstyle.region.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.region.CuboidRegion;
import xyz.acrylicstyle.region.api.selection.SelectionMode;
import xyz.acrylicstyle.tomeito_core.command.PlayerCommandExecutor;
import xyz.acrylicstyle.tomeito_core.utils.TypeUtil;

import java.util.Arrays;

public class ExpandCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        if (args.length <= 1) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + "//expand <number> <up/down/east/south/west/north>");
            player.sendMessage(ChatColor.YELLOW + "Expands selection area by <number>.");
            return;
        }
        if (RegionEdit.getInstance().getUserSession(player).getSelectionMode() != SelectionMode.CUBOID) {
            player.sendMessage(ChatColor.RED + "Selection mode must be cuboid.");
            return;
        }
        CuboidRegion region = RegionEdit.getInstance().getUserSession(player).getCuboidRegion();
        if (!region.isValid()) {
            player.sendMessage(ChatColor.RED + "You must make selection first.");
            return;
        }
        if (args[0].equalsIgnoreCase("vert")) {
            region.getLocation().setY(0);
            region.getLocation2().setY(255);
            RegionEditPlugin.showCurrentRegion(player);
            return;
        }
        if (!TypeUtil.isInt(args[0])) {
            player.sendMessage(ChatColor.RED + "Error: Number must be integer.");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "//expand <number> <up/down/east/south/west/north>");
            player.sendMessage(ChatColor.YELLOW + "Expands selection area by <number>.");
            return;
        }
        if (!Arrays.asList("up", "down", "east", "south", "west", "north").contains(args[1].toLowerCase())) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + "//expand <number> <up/down/east/south/west/north>");
            player.sendMessage(ChatColor.YELLOW + "Expands selection area by <number>.");
            return;
        }
        int number = Integer.parseInt(args[0]);
        if (args[1].equalsIgnoreCase("up")) { // y+
            if (region.getLocation().getY() >= region.getLocation2().getY()) {
                region.getLocation().setY(Math.min(region.getLocation().getY() + number, 255));
            } else {
                region.getLocation2().setY(Math.min(region.getLocation2().getY() + number, 255));
            }
        } else if (args[1].equalsIgnoreCase("down")) { // y-
            if (region.getLocation().getY() <= region.getLocation2().getY()) {
                region.getLocation().setY(Math.max(region.getLocation().getY() - number, 0));
            } else {
                region.getLocation2().setY(Math.max(region.getLocation2().getY() - number, 0));
            }
        } else if (args[1].equalsIgnoreCase("east")) { // x+
            if (region.getLocation().getX() >= region.getLocation2().getX()) {
                region.getLocation().setX(region.getLocation().getX() + number);
            } else {
                region.getLocation2().setX(region.getLocation2().getX() + number);
            }
        } else if (args[1].equalsIgnoreCase("south")) { // z+
            if (region.getLocation().getZ() >= region.getLocation2().getZ()) {
                region.getLocation().setZ(region.getLocation().getZ() + number);
            } else {
                region.getLocation2().setZ(region.getLocation2().getZ() + number);
            }
        } else if (args[1].equalsIgnoreCase("west")) { // x-
            if (region.getLocation().getX() <= region.getLocation2().getX()) {
                region.getLocation().setX(region.getLocation().getX() - number);
            } else {
                region.getLocation2().setX(region.getLocation2().getX() - number);
            }
        } else if (args[1].equalsIgnoreCase("north")) { // z-
            if (region.getLocation().getZ() <= region.getLocation2().getZ()) {
                region.getLocation().setZ(region.getLocation().getZ() - number);
            } else {
                region.getLocation2().setZ(region.getLocation2().getZ() - number);
            }
        }
        RegionEditPlugin.showCurrentRegion(player);
    }
}
