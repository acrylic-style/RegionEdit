package xyz.acrylicstyle.region.api.manager;

import org.bukkit.Location;
import util.Collection;
import util.CollectionList;
import xyz.acrylicstyle.region.api.block.Block;

import java.util.UUID;

public interface HistoryManager {
    /**
     * Adds a entry into history.
     * @param player UUID of a player.
     * @param blocks Blocks before modify
     */
    void addEntry(UUID player, CollectionList<Block> blocks);

    /**
     * Get location : block pair of history.
     * @param player UUID of a player.
     * @return Blocks
     */
    Collection<Location, Block> get(UUID player);

    /**
     * Get location : block pair of history when doing undo operation.
     * @param uuid UUID of a player.
     * @return Blocks
     */
    Collection<Location, Block> getUndo(UUID uuid);

    /**
     * Returns previous(redo) entry.
     * @param uuid UUID of a player.
     * @return Blocks
     */
    Collection<Location, Block> previous(UUID uuid);

    /**
     * Returns next(undo) entry.
     * @param uuid UUID of a player.
     * @return Blocks
     */
    Collection<Location, Block> next(UUID uuid);

    /**
     * Reset history pointer for player.
     * @param uuid UUID of a player.
     */
    void resetPointer(UUID uuid);
}
