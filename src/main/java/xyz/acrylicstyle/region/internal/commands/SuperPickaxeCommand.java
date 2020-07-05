package xyz.acrylicstyle.region.internal.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.player.SuperPickaxeMode;
import xyz.acrylicstyle.region.api.player.UserSession;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;
import xyz.acrylicstyle.tomeito_api.utils.TypeUtil;

public class SuperPickaxeCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        UserSession session = RegionEdit.getInstance().getUserSession(player);
        if (args.length != 0) {
            if (args[0].equalsIgnoreCase("area")) {
                if (args.length == 1) {
                    player.sendMessage(ChatColor.RED + "/sp area <radius>");
                    return;
                }
                int radius = radiusCheck(player, args[1]);
                if (radius == -1) return;
                session.setSuperPickaxeMode(SuperPickaxeMode.AREA);
                session.setSuperPickaxeRadius(radius);
                player.sendMessage(ChatColor.GREEN + "Super pickaxe mode was set to " + ChatColor.GOLD + "area " + ChatColor.GREEN + "mode and radius to " + ChatColor.YELLOW + radius + ChatColor.GREEN + ".");
            } else if (args[0].equalsIgnoreCase("single")) {
                session.setSuperPickaxeMode(SuperPickaxeMode.SINGLE);
                player.sendMessage(ChatColor.GREEN + "Super pickaxe mode was set to " + ChatColor.GOLD + "single " + ChatColor.GREEN + "mode.");
            } else if (args[0].equalsIgnoreCase("drop") || args[0].equalsIgnoreCase("area+drop")) {
                if (args.length == 1) {
                    player.sendMessage(ChatColor.RED + "/sp drop <radius>");
                    return;
                }
                int radius = radiusCheck(player, args[1]);
                if (radius == -1) return;
                session.setSuperPickaxeMode(SuperPickaxeMode.AREA_DROP);
                session.setSuperPickaxeRadius(radius);
                player.sendMessage(ChatColor.GREEN + "Super pickaxe mode was set to " + ChatColor.GOLD + "area+drop " + ChatColor.GREEN + "mode and radius to " + ChatColor.YELLOW + radius + ChatColor.GREEN + ".");
            } else if (args[0].equalsIgnoreCase("off")) {
                session.setSuperPickaxeMode(SuperPickaxeMode.OFF);
                player.sendMessage(ChatColor.GREEN + "Turned off super pickaxe.");
            } else {
                player.sendMessage(ChatColor.LIGHT_PURPLE + "Super Pickaxe commands:");
                player.sendMessage(ChatColor.GREEN + "/sp " + ChatColor.GOLD + "area " + ChatColor.YELLOW + "<radius> "
                        + ChatColor.GRAY + "- " + ChatColor.AQUA + "Breaks blocks in specified radius. Does not drop items.");
                player.sendMessage(ChatColor.GREEN + "/sp " + ChatColor.GOLD + "single "
                        + ChatColor.GRAY + "- " + ChatColor.AQUA + "Breaks single block, and drops item.");
                player.sendMessage(ChatColor.GREEN + "/sp " + ChatColor.GOLD + "drop " + ChatColor.YELLOW + "<radius> "
                        + ChatColor.GRAY + "- " + ChatColor.AQUA + "Similar to area mode, but it also drops the items.");
                player.sendMessage(ChatColor.GREEN + "/sp " + ChatColor.GOLD + "off "
                        + ChatColor.GRAY + "- " + ChatColor.AQUA + "Turn off super pickaxe.");
            }
            return;
        }
        if (session.getSuperPickaxeMode() == SuperPickaxeMode.OFF) {
            session.setSuperPickaxeMode(SuperPickaxeMode.AREA);
            player.sendMessage(ChatColor.GREEN + "Turned on super pickaxe.");
        } else {
            session.setSuperPickaxeMode(SuperPickaxeMode.OFF);
            player.sendMessage(ChatColor.GREEN + "Turned off super pickaxe.");
        }
    }

    private int radiusCheck(Player player, String s) {
        if (!TypeUtil.isInt(s)) {
            player.sendMessage(ChatColor.RED + "That isn't a valid number.");
            return -1;
        }
        int radius = Integer.parseInt(s);
        if (radius < 1) {
            player.sendMessage(ChatColor.RED + "A number must be higher than 0.");
            return -1;
        }
        if (radius > 10) {
            player.sendMessage(ChatColor.RED + "A number must be lower than 10.");
            return -1;
        }
        return radius;
    }
}
