package xyz.acrylicstyle.region.internal.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import util.Collection;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.block.Block;
import xyz.acrylicstyle.tomeito_core.command.PlayerCommandExecutor;

public class UndoCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        Collection<Location, Block> blocks = RegionEdit.getInstance().getHistoryManager().next(player.getUniqueId());
        if (blocks == null) {
            player.sendMessage(ChatColor.RED + "There's nothing to undo.");
            return;
        }
        RegionEditPlugin.setBlocks(player, blocks);
    }
}
