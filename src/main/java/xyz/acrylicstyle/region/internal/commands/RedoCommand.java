package xyz.acrylicstyle.region.internal.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import util.Collection;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.block.state.BlockState;
import xyz.acrylicstyle.region.api.util.BlockPos;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;

public class RedoCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        Collection<BlockPos, BlockState> blocks = RegionEdit.getInstance().getHistoryManager().previous(player.getUniqueId());
        if (blocks == null) {
            player.sendMessage(ChatColor.RED + "There's nothing to redo.");
            return;
        }
        RegionEdit.pool.execute(() -> RegionEditPlugin.setBlocks(player, blocks));
    }
}
