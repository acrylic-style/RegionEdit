package xyz.acrylicstyle.region.internal.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import util.ICollectionList;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.internal.command.CommandDescription;
import xyz.acrylicstyle.region.internal.utils.Compatibility;

public class RegionEditCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/regionedit <help/version/reload/commands/compatibility>");
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
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "Version: " + ChatColor.YELLOW + Compatibility.getBukkitVersion().getName());
            if (Compatibility.checkPlayerInventory_getItemInHand()) {
                sender.sendMessage(ChatColor.GREEN + " ✔ " + ChatColor.YELLOW + "Found Inventory#getItemInHand (1.8 - 1.12.2)");
            } else {
                sender.sendMessage(ChatColor.RED + " ✖ " + ChatColor.YELLOW + "Not Found Inventory#getItemInHand (1.13+)");
            }
            if (Compatibility.checkPlayerInteractEvent_getHand()) {
                sender.sendMessage(ChatColor.GREEN + " ✔ " + ChatColor.YELLOW + "Found PlayerInteractEvent#getHand (1.9+)");
            } else {
                sender.sendMessage(ChatColor.RED + " ✖ " + ChatColor.YELLOW + "Not Found PlayerInteractEvent#getHand (1.8)");
            }
            if (Compatibility.checkBlock_getData()) {
                sender.sendMessage(ChatColor.GREEN + " ✔ " + ChatColor.YELLOW + "Found Block#getData (1.8+)");
            } else {
                sender.sendMessage(ChatColor.RED + " ✖ " + ChatColor.YELLOW + "Not Found Block#getData (?)");
            }
            if (Compatibility.checkBlockData()) {
                sender.sendMessage(ChatColor.GREEN + " ✔ " + ChatColor.YELLOW + "Found BlockData (1.13+)");
            } else {
                sender.sendMessage(ChatColor.RED + " ✖ " + ChatColor.YELLOW + "Not Found BlockData (1.8 - 1.12.2)");
            }
            if (Compatibility.checkOldPlayer_sendBlockChange()) {
                sender.sendMessage(ChatColor.GREEN + " ✔ " + ChatColor.YELLOW + "Found Player#sendBlockChange(Location, Material, byte) (1.8+)");
            } else {
                sender.sendMessage(ChatColor.RED + " ✖ " + ChatColor.YELLOW + "Not Found Player#sendBlockChange(Location, Material, byte) (?)");
            }
            if (Compatibility.checkNewPlayer_sendBlockChange()) {
                sender.sendMessage(ChatColor.GREEN + " ✔ " + ChatColor.YELLOW + "Found Player#sendBlockChange(Location, BlockData) (1.13 - 1.15.2)");
            } else {
                sender.sendMessage(ChatColor.RED + " ✖ " + ChatColor.YELLOW + "Not Found Player#sendBlockChange(Location, BlockData) (1.8 - 1.12.2)");
            }
            if (Compatibility.checkWorld_notify()) {
                sender.sendMessage(ChatColor.GREEN + " ✔ " + ChatColor.YELLOW + "Found nms.World#notify(BlockPosition) (1.8)");
            } else {
                sender.sendMessage(ChatColor.RED + " ✖ " + ChatColor.YELLOW + "Not Found nms.World#notify(BlockPosition) (1.9+)");
            }
            if (Compatibility.checkChunk_markDirty()) {
                sender.sendMessage(ChatColor.GREEN + " ✔ " + ChatColor.YELLOW + "Found nms.Chunk#markDirty() (1.9+)");
            } else {
                sender.sendMessage(ChatColor.RED + " ✖ " + ChatColor.YELLOW + "Not Found nms.Chunk#markDirty() (1.8)");
            }
            if (Compatibility.checkPacketPlayOutMapChunkOldConstructor()) {
                sender.sendMessage(ChatColor.GREEN + " ✔ " + ChatColor.YELLOW + "Found old nms.PacketPlayOutMapChunk constructor (1.8)");
            } else {
                sender.sendMessage(ChatColor.RED + " ✖ " + ChatColor.YELLOW + "Not Found old nms.PacketPlayOutMapChunk constructor (1.9+)");
            }
            if (Compatibility.checkOldChunkSectionConstructor()) {
                sender.sendMessage(ChatColor.GREEN + " ✔ " + ChatColor.YELLOW + "Found old nms.ChunkSection constructor (1.8 - 1.13.2)");
            } else {
                sender.sendMessage(ChatColor.RED + " ✖ " + ChatColor.YELLOW + "Not Found old nms.ChunkSection constructor (1.14+)");
            }
            if (Compatibility.checkLightEngine()) {
                sender.sendMessage(ChatColor.GREEN + " ✔ " + ChatColor.YELLOW + "Found LightEngine (1.13+)");
            } else {
                sender.sendMessage(ChatColor.RED + " ✖ " + ChatColor.YELLOW + "Not Found LightEngine (1.8 - 1.12.2)");
            }
            if (Compatibility.checkChunk_setType()) {
                sender.sendMessage(ChatColor.GREEN + " ✔ " + ChatColor.YELLOW + "Found nms.Chunk#setType (1.13.2+)");
            } else {
                sender.sendMessage(ChatColor.RED + " ✖ " + ChatColor.YELLOW + "Not Found nms.Chunk#setType (1.8 - 1.13.1)");
            }
            if (Compatibility.checkStationary_Water()) {
                sender.sendMessage(ChatColor.GREEN + " ✔ " + ChatColor.YELLOW + "Found STATIONARY_WATER in Material enum (1.8 - 1.12.2)");
            } else {
                sender.sendMessage(ChatColor.RED + " ✖ " + ChatColor.YELLOW + "Not Found STATIONARY_WATER in Material enum (1.13+)");
            }
            sender.sendMessage(ChatColor.GOLD + "-------------------------");
            if (Compatibility.checkChunk_setType()) {
                sender.sendMessage(ChatColor.YELLOW + " - Modify blocks using " + ChatColor.DARK_PURPLE + "Chunk#setType");
            } else {
                sender.sendMessage(ChatColor.YELLOW + " - Modify blocks using " + ChatColor.LIGHT_PURPLE + "ChunkSection#setType");
            }
            if (Compatibility.checkLightEngine()) {
                sender.sendMessage(ChatColor.YELLOW + " - Does " + ChatColor.RED + "not " + ChatColor.YELLOW + "perform light update");
            } else {
                sender.sendMessage(ChatColor.YELLOW + " - Does perform light update");
            }
            if (Compatibility.checkNewPlayer_sendBlockChange()) {
                sender.sendMessage(ChatColor.YELLOW + " - Uses new Player#sendBlockChange to send block changes");
            } else {
                sender.sendMessage(ChatColor.YELLOW + " - Uses old Player#sendBlockChange to send block changes");
            }
            sender.sendMessage(ChatColor.GOLD + "-------------------------");
        } else if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("regions.reload")) {
                sender.sendMessage(ChatColor.GREEN + "You don't have following permission: " + ChatColor.YELLOW + "regions.reload");
                return true;
            }
            RegionEdit.getInstance().reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "Reloaded the configuration.");
        } else sender.sendMessage(ChatColor.LIGHT_PURPLE + "/regionedit <help/version/reload/commands/compatibility>");
        return true;
    }
}
