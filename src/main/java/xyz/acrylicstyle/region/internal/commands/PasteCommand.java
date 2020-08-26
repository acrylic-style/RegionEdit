package xyz.acrylicstyle.region.internal.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import util.Collection;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.AsyncCatcher;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.block.state.BlockState;
import xyz.acrylicstyle.region.api.player.UserSession;
import xyz.acrylicstyle.region.api.util.BlockPos;
import xyz.acrylicstyle.region.api.util.Tuple;
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
        final Location l1 = player.getLocation();
        AsyncCatcher.setEnabled(false);
        RegionEdit.pool.execute(() -> {
            Collection<BlockPos, BlockState> blocks = new Collection<>();
            session.getClipboard().forEach(state -> {
                int x = l1.getBlockX() + state.getLocation().getX();
                int y = l1.getBlockY() + state.getLocation().getY();
                int z = l1.getBlockZ() + state.getLocation().getZ();
                BlockPos loc = new BlockPos(l1.getWorld(), x, y, z);
                blocks.add(loc, new BlockState(state, new Tuple<>(x, y, z)));
            });
            player.sendMessage(ChatColor.GREEN + "Pasting clipboard... (it may take a while!)");
            try {
                RegionEditPlugin.setBlocks(player, blocks, true);
            } catch (OutOfMemoryError e) {
                RegionEditPlugin.reserve.freeImmediately();
                blocks.clear();
                System.gc();
                RegionEdit.getInstance().getUserSession(player).setClipboard(null);
                System.gc();
                player.sendMessage(ChatColor.RED + "It looks like you tried to paste a lot of blocks. Clipboard has been cleared.");
            }
        });
    }
}
