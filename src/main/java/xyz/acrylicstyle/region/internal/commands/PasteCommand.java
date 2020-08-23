package xyz.acrylicstyle.region.internal.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import util.Collection;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.AsyncCatcher;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.block.Block;
import xyz.acrylicstyle.region.api.player.UserSession;
import xyz.acrylicstyle.region.internal.block.RegionBlock;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;

public class PasteCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        UserSession session = RegionEdit.getInstance().getUserSession(player);
        if (session.getClipboard() == null) {
            player.sendMessage(ChatColor.RED + "Clipboard is empty.");
            return;
        }
        player.sendMessage(ChatColor.GREEN + "Calculating blocks...");
        AsyncCatcher.setEnabled(false);
        RegionEdit.pool.execute(() -> {
            Collection<Location, Block> blocks = new Collection<>();
            Location l1 = player.getLocation();
            session.getClipboard().forEach(state -> { // air run
                Location l2 = state.getLocation();
                Location loc = new Location(l1.getWorld(), l1.getBlockX() + l2.getBlockX(), l1.getBlockY() + l2.getBlockY(), l1.getBlockZ() + l2.getBlockZ());
                new RegionBlock(loc, state.getType(), state.getData(), null);
            });
            session.getClipboard().forEach(state -> {
                Location l2 = state.getLocation();
                Location loc = new Location(l1.getWorld(), l1.getBlockX() + l2.getBlockX(), l1.getBlockY() + l2.getBlockY(), l1.getBlockZ() + l2.getBlockZ());
                RegionBlock block = new RegionBlock(loc, state.getType(), state.getData(), null);
                blocks.add(loc, block);
            });
            player.sendMessage(ChatColor.GREEN + "Pasting clipboard... (it may take a while!)");
            RegionEditPlugin.setBlocks(player, blocks, true);
        });
    }
}
