package xyz.acrylicstyle.region.api.block;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public interface BlockData {
    /**
     * Returns bukkit implementation of block.
     * @return Bukkit implementation of block.
     */
    @NotNull
    org.bukkit.block.Block getBukkitBlock();

    /**
     * Returns parent block.
     * @return Parent block
     */
    @NotNull
    Block getBlock();

    /**
     * Returns block data represented as JSON.
     * @return Block data represented as JSON
     */
    @NotNull
    String getAsString();

    /**
     * Returns block data represented as JSON.
     * @param paramBoolean Plain or not
     * @return Block data represented as JSON
     */
    @NotNull
    String getAsString(boolean paramBoolean);

    /**
     * Returns material.
     * @return Material
     */
    @NotNull
    Material getMaterial();

    /**
     * Returns nms BlockState.
     * @return NMS BlockState
     */
    @NotNull
    Object getState();

    /**
     * Merges two BlockData.<br />
     * Returns merged BlockData.
     * @param paramBlockData 2nd BlockData.
     * @return Merged BlockData
     */
    @NotNull
    BlockData merge(@NotNull BlockData paramBlockData);

    /**
     * Returns if does two block data matches.
     * @param paramBlockData Block data that compares to.
     */
    boolean matches(@NotNull BlockData paramBlockData);
}
