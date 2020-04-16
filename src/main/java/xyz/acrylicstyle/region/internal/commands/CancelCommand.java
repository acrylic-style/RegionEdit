package xyz.acrylicstyle.region.internal.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import util.Collection;
import util.CollectionList;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.operation.OperationStatus;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;
import xyz.acrylicstyle.tomeito_api.utils.TypeUtil;

public class CancelCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        if (!player.hasPermission("regions.cancel.self")) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + "You need following permission: " + ChatColor.AQUA + "regions.cancel.self");
            return;
        }
        if (!RegionEditPlugin.playerTasks.containsKey(player.getUniqueId()))
            RegionEditPlugin.playerTasks.add(player.getUniqueId(), new CollectionList<>());
        if (args.length == 0) {
            if (RegionEditPlugin.tasks.get(RegionEditPlugin.playerTasks.get(player.getUniqueId()).last()) == OperationStatus.FINISHED) {
                player.sendMessage(ChatColor.RED + "The task is already finished.");
                return;
            }
            RegionEditPlugin.tasks.add(RegionEditPlugin.playerTasks.get(player.getUniqueId()).last(), OperationStatus.CANCELLED);
            player.sendMessage(ChatColor.GREEN + "Cancelled the operation.");
            return;
        }
        if (args[0].equalsIgnoreCase("all")) {
            if (!player.hasPermission("regions.cancel.c")) {
                player.sendMessage(ChatColor.LIGHT_PURPLE + "You need following permission: " + ChatColor.AQUA + "regions.cancel.c");
                return;
            }
            Collection<Integer, OperationStatus> running = RegionEditPlugin.tasks
                    .clone()
                    .filter((status) -> status == OperationStatus.RUNNING);
            running.forEach((i, status) -> RegionEditPlugin.tasks.add(i, OperationStatus.CANCELLED));
            player.sendMessage(ChatColor.GREEN + "Cancelled all running operations. " + ChatColor.LIGHT_PURPLE + "(" + running.size() + " operations were running)");
            return;
        }
        if (!player.hasPermission("regions.cancel.b")) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + "You need following permission: " + ChatColor.AQUA + "regions.cancel.b");
            return;
        }
        if (!TypeUtil.isInt(args[0])) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /cancel [task id / all]");
            player.sendMessage(ChatColor.YELLOW + "Cancels the operation.");
            return;
        }
        int taskId = Integer.parseInt(args[0]);
        if (!RegionEditPlugin.tasks.containsKey(taskId)) {
            player.sendMessage(ChatColor.RED + "Couldn't find task by specified task ID.");
            return;
        }
        if (RegionEditPlugin.tasks.get(taskId) == OperationStatus.CANCELLED) {
            player.sendMessage(ChatColor.RED + "The task is already finished.");
            return;
        }
        RegionEditPlugin.tasks.add(taskId, OperationStatus.CANCELLED);
        player.sendMessage(ChatColor.GREEN + "Cancelled the operation.");
    }
}
