package xyz.acrylicstyle.region.api.manager;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
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
    void addEntry(@NotNull UUID player, @NotNull CollectionList<Block> blocks);

    /**
     * Get location : block pair of history.
     * @param player UUID of a player.
     * @return Blocks
     */
    Collection<Location, Block> get(@NotNull UUID player);

    /**
     * Get location : block pair of history when doing undo operation.
     * @param uuid UUID of a player.
     * @return Blocks
     */
    Collection<Location, Block> getUndo(@NotNull UUID uuid);

    /**
     * Returns previous(redo) entry.
     * @param uuid UUID of a player.
     * @return Blocks
     */
    Collection<Location, Block> previous(@NotNull UUID uuid);

    /**
     * Returns next(undo) entry.
     * @param uuid UUID of a player.
     * @return Blocks
     */
    Collection<Location, Block> next(@NotNull UUID uuid);

    /**
     * Reset history pointer for player.
     * @param uuid UUID of a player.
     */
    void resetPointer(@NotNull UUID uuid);
}
