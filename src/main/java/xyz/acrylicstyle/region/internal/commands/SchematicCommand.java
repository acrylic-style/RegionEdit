package xyz.acrylicstyle.region.internal.commands;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import util.ICollectionList;
import util.Watchdog;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.block.state.BlockState;
import xyz.acrylicstyle.region.api.schematic.Schematic;
import xyz.acrylicstyle.region.api.schematic.SchematicManager;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;

import java.io.File;

public class SchematicCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(@NotNull Player player, String[] args) {
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
            new Watchdog("Schematic->load@" + player.getName(), () -> {
                player.sendMessage(ChatColor.LIGHT_PURPLE + "Loading schematic...");
                File file = SchematicManager.findSchematic(args[1]);
                if (file == null) {
                    player.sendMessage(ChatColor.RED + "Schematic " + ChatColor.YELLOW + args[1] + ".schem " + ChatColor.RED + "doesn't exist.");
                    return;
                }
                Schematic schematic = SchematicManager.load(file);
                if (schematic == null) {
                    player.sendMessage(ChatColor.RED + "Schematic " + ChatColor.YELLOW + args[1] + ".schem " + ChatColor.RED + "doesn't exist.");
                    return;
                }
                @NotNull ICollectionList<BlockState> blocks = schematic.getBlocks();
                RegionEdit.getInstance().getUserSession(player).setClipboard(blocks);
                player.sendMessage(ChatColor.GREEN + "Loaded schematic " + ChatColor.YELLOW + args[1] + ChatColor.GREEN + ". Run " + ChatColor.GOLD + "//paste " + ChatColor.GREEN + "to paste it.");
            }, 60000, () -> player.sendMessage(ChatColor.RED + "Loading of the schematic was timed out. (took 60 seconds)")).start();
        } else if (args[0].equals("list")) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Schematics:");
            SchematicManager.findSchematicFiles().forEach(s -> {
                TextComponent text = new TextComponent();
                TextComponent load = new TextComponent(ChatColor.GOLD + "[L] ");
                load.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{ new TextComponent(ChatColor.GREEN + "Loads the schematic into the clipboard.") }));
                load.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + RegionEditPlugin.COMMAND_PREFIX + "schem load " + s.replaceFirst("(.*)\\.(schem|schematic)", "$1")));
                TextComponent name = new TextComponent(ChatColor.LIGHT_PURPLE + s);
                text.addExtra(load);
                text.addExtra(name);
                player.spigot().sendMessage(text);
            });
        } else {
            player.sendMessage(ChatColor.RED + "Error: Unknown command " + ChatColor.YELLOW + args[0] + ChatColor.RED + ".");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /" + RegionEditPlugin.COMMAND_PREFIX + "schem <load|list>");
        }
    }
}
