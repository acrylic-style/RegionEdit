package xyz.acrylicstyle.region.api.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Block {
    /**
     * For 1.8, returns null.<br />
     * For 1.9+, returns block data.
     */
    @Nullable
    BlockData getBlockData();

    /**
     * Returns bukkit implementation of block.
     * @return Bukkit implementation of block
     */
    @NotNull
    org.bukkit.block.Block getBukkitBlock();

    /**
     * Set block data when server version is 1.9+.<br />
     * This method does nothing if server does not support BlockData.
     */
    void setBlockData(BlockData blockData);

    /**
     * Set type and data.
     * @param material Material type
     * @param b Item data byte
     * @param blockData Block data
     * @param applyPhysics Applies physics or not
     */
    void setTypeAndData(@NotNull Material material, byte b, BlockData blockData, boolean applyPhysics);

    /**
     * Set type and data.<br />
     * For 1.8 - 1.12.2, it does nothing.
     * @param blockData Block data
     * @param applyPhysics Applies physics or not
     */
    void setTypeAndData(@NotNull BlockData blockData, boolean applyPhysics);

    /**
     * Set type and data.<br />
     * For 1.13+, it does nothing.
     * @param material Material type
     * @param b Item data byte
     * @param applyPhysics Applies physics or not
     */
    void setTypeAndData(@NotNull Material material, byte b, boolean applyPhysics);

    /**
     * Returns location of this block.
     * @return Location of this block
     */
    @NotNull
    Location getLocation();

    /**
     * Returns material of this block
     * @return Material(type) of this block
     */
    @NotNull
    Material getType();

    /**
     * Returns data of this block.<br />
     * @return Block data
     */
    byte getData();

    /**
     * Clones this block.
     */
    Block clone();
}
