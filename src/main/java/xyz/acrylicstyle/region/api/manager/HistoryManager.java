package xyz.acrylicstyle.region.api.manager;

import org.jetbrains.annotations.NotNull;
import util.Collection;
import util.ICollectionList;
import xyz.acrylicstyle.region.api.block.Block;
import xyz.acrylicstyle.region.api.block.state.BlockState;
import xyz.acrylicstyle.region.api.util.BlockPos;

import java.util.UUID;

public interface HistoryManager {
    /**
     * Adds a entry into history.
     * @param player UUID of a player.
     * @param blocks Blocks before modify
     */
    void addEntry(@NotNull UUID player, @NotNull ICollectionList<Block> blocks);

    /**
     * Adds a entry into history.
     * @param player UUID of a player.
     * @param blocks Blocks before modify
     */
    void addEntry(@NotNull UUID player, @NotNull Collection<BlockPos, BlockState> blocks);

    /**
     * Get location : block pair of history.
     * @param player UUID of a player.
     * @return Blocks
     */
    Collection<BlockPos, BlockState> get(@NotNull UUID player);

    /**
     * Get location : block pair of history when doing undo operation.
     * @param uuid UUID of a player.
     * @return Blocks
     */
    Collection<BlockPos, BlockState> getUndo(@NotNull UUID uuid);

    /**
     * Returns previous(redo) entry.
     * @param uuid UUID of a player.
     * @return Blocks
     */
    Collection<BlockPos, BlockState> previous(@NotNull UUID uuid);

    /**
     * Returns next(undo) entry.
     * @param uuid UUID of a player.
     * @return Blocks
     */
    Collection<BlockPos, BlockState> next(@NotNull UUID uuid);

    /**
     * Reset history pointer for player.
     * @param uuid UUID of a player.
     */
    void resetPointer(@NotNull UUID uuid);
}
