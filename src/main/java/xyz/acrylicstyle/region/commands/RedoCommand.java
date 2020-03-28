package xyz.acrylicstyle.region.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import util.Collection;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.tomeito_core.command.PlayerCommandExecutor;

public class RedoCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        Collection<Location, Block> blocks = RegionEdit.getInstance().getHistoryManager().previous(player.getUniqueId());
        if (blocks == null) {
            player.sendMessage(ChatColor.RED + "There's nothing to redo.");
            return;
        }
        RegionEditPlugin.setBlocks(player, blocks);
    }
}
