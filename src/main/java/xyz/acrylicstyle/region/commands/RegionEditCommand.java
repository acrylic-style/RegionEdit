package xyz.acrylicstyle.region.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import util.ICollectionList;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.internal.commands.CommandDescription;
import xyz.acrylicstyle.region.internal.utils.Compatibility;

public class RegionEditCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/regionedit <help/version/reload>");
            return true;
        }
        if (args[0].equalsIgnoreCase("help")) {
            if (!sender.hasPermission("regions.help")) {
                sender.sendMessage(ChatColor.GREEN + "You don't have following permission: " + ChatColor.YELLOW + "regions.help");
                return true;
            }
            if (args.length == 1) {
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "/regionedit help <command>");
                sender.sendMessage(ChatColor.YELLOW + "Shows help of the command.");
                return true;
            }
            if (!RegionEditPlugin.commandDescriptionManager.containsKey(args[1].toLowerCase())) {
                sender.sendMessage(ChatColor.RED + "Error: Invalid command.");
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "/regionedit help <command>");
                sender.sendMessage(ChatColor.YELLOW + "Shows help of the command.");
                return true;
            }
            CommandDescription description = RegionEditPlugin.commandDescriptionManager.get(args[1].toLowerCase());
            sender.sendMessage(ChatColor.GREEN + "Usage: " + ChatColor.LIGHT_PURPLE + description.getName());
            sender.sendMessage(ChatColor.GREEN + "Permissions:");
            sender.sendMessage(description.getPermissionsAsString());
            description.getDescription().forEach(s -> sender.sendMessage(ChatColor.YELLOW + s));
        } else if (args[0].equalsIgnoreCase("commands")) {
            if (!sender.hasPermission("regions.help")) {
                sender.sendMessage(ChatColor.GREEN + "You don't have following permission: " + ChatColor.YELLOW + "regions.help");
                return true;
            }
            HelpCommand.showHelp(sender, args.length != 1 ? args[1] : null);
        } else if (args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("ver")) {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "Region Edit " + ChatColor.YELLOW + "v" + RegionEdit.getInstance().getDescription().getVersion());
            sender.sendMessage(ChatColor.GREEN + "Authors: " + ICollectionList.asList(RegionEdit.getInstance().getDescription().getAuthors()).join(ChatColor.YELLOW + ", " + ChatColor.GREEN));
        } else if (args[0].equalsIgnoreCase("compatibility")) {
            if (!sender.hasPermission("regions.compatibility")) {
                sender.sendMessage(ChatColor.GREEN + "You don't have following permission: " + ChatColor.YELLOW + "regions.compatibility");
                return true;
            }
            sender.sendMessage(ChatColor.GOLD + "----- Compatibility -----");
            if (Compatibility.checkPlayerInventory_getItemInHand()) {
                sender.sendMessage(ChatColor.RED + " ✖ " + ChatColor.YELLOW + "Not Found Inventory#getItemInHand (1.13+)");
            } else {
                sender.sendMessage(ChatColor.GREEN + " ✔ " + ChatColor.YELLOW + "Found Inventory#getItemInHand (1.8 - 1.12.2)");
            }
            if (Compatibility.checkPlayerInteractEvent_getHand()) {
                sender.sendMessage(ChatColor.RED + " ✖ " + ChatColor.YELLOW + "Not Found PlayerInteractEvent#getHand (1.8)");
            } else {
                sender.sendMessage(ChatColor.GREEN + " ✔ " + ChatColor.YELLOW + "Found PlayerInteractEvent#getHand (1.9+)");
            }
            if (Compatibility.checkBlock_getData()) {
                sender.sendMessage(ChatColor.RED + " ✖ " + ChatColor.YELLOW + "Not Found Block#getData (1.13+)");
            } else {
                sender.sendMessage(ChatColor.GREEN + " ✔ " + ChatColor.YELLOW + "Found Block#getData (1.8 - 1.12.2)");
            }
            sender.sendMessage(ChatColor.GOLD + "-------------------------");
        } else if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("regions.reload")) {
                sender.sendMessage(ChatColor.GREEN + "You don't have following permission: " + ChatColor.YELLOW + "regions.reload");
                return true;
            }
            RegionEdit.getInstance().reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "Reloaded the configuration.");
        } else sender.sendMessage(ChatColor.YELLOW + "/regionedit <help/version/reload/commands/compatibility>");
        return true;
    }
}
