package xyz.acrylicstyle.region.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import util.CollectionList;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.tomeito_core.utils.TypeUtil;

public class HelpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        showHelp(sender, args.length == 0 ? null : args[0]);
        return true;
    }

    static void showHelp(CommandSender sender, String pageStr) {
        int page = 1;
        if (pageStr != null) {
            if (!TypeUtil.isInt(pageStr)) {
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "//help [page]");
                sender.sendMessage(ChatColor.YELLOW + "Shows all RegionEdit commands.");
                return;
            }
            page = Integer.parseInt(pageStr);
            if (page < 1) {
                sender.sendMessage(ChatColor.RED + "Error: Help Page cannot be lower than 1.");
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "//help [page]");
                sender.sendMessage(ChatColor.YELLOW + "Shows all RegionEdit commands.");
                return;
            }
        }
        sender.sendMessage(ChatColor.GOLD + String.format("----- All RegionEdit commands [Page %d] -----", page));
        int finalPage = page;
        CollectionList<String> messages = new CollectionList<>();
        RegionEditPlugin.commandDescriptionManager.forEach((cmd, description, i, a) -> {
            if (i > 12*(finalPage-1) && i < 12*finalPage) messages.add(ChatColor.translateAlternateColorCodes('&', String.format("&a%s &8- &e%s", description.getName(), description.getDescription().first())));
        });
        if (messages.size() == 0) {
            sender.sendMessage(ChatColor.RED + "No commands were found in this page.");
        } else messages.forEach(sender::sendMessage);
    }
}
