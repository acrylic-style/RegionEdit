package xyz.acrylicstyle.region.api.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.ReflectionHelper;
import xyz.acrylicstyle.region.internal.utils.Compatibility;
import xyz.acrylicstyle.region.internal.utils.Reflection;

import java.lang.reflect.InvocationTargetException;

public class Block {
    @NotNull
    private final Location location;
    @NotNull
    private final Material type;
    private final byte data;
    @Nullable
    private final BlockData blockData;

    public Block(@NotNull org.bukkit.block.Block block) {
        this.location = block.getLocation();
        this.type = block.getType();
        this.data = Reflection.getData(block);
        this.blockData = Reflection.getBlockData(block);
    }

    @NotNull
    @Contract("_ -> new")
    public static Block wrap(@NotNull org.bukkit.block.Block block) {
        return new Block(block);
    }

    public Block(@NotNull Location location, @NotNull Material type, byte data, @Nullable BlockData blockData) {
        this.location = location;
        this.type = type;
        this.data = data;
        this.blockData = blockData;
    }

    /**
     * For 1.8, returns null.<br />
     * For 1.9+, returns block data.
     */
    @Nullable
    public BlockData getBlockData() {
        return blockData;
    }

    @NotNull
    public org.bukkit.block.Block getBukkitBlock() {
        return this.getLocation().getBlock();
    }

    /**
     * Set block data when server version is 1.9+.<br />
     * This method does nothing if server does not support BlockData.
     */
    public void setBlockData(BlockData blockData) {
        if (!Compatibility.checkBlockData()) return;
        if (blockData == null) throw new NullPointerException("blockData is null!"); // BlockData class is available so blockData shouldn't be null.
        try {
            ReflectionHelper.invokeMethod(location.getBlock().getClass(), location.getBlock(), "setBlockData", blockData.getData());
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set type and data.
     * @param material Material type
     * @param b Item data byte
     * @param blockData Block data
     * @param applyPhysics Applies physics or not
     */
    public void setTypeAndData(@NotNull Material material, byte b, BlockData blockData, boolean applyPhysics) {
        if (Compatibility.checkBlockData() && blockData != null) {
            setTypeAndData(blockData, applyPhysics);
        } else {
            setTypeAndData(material, b, applyPhysics);
        }
    }

    /**
     * Set type and data.<br />
     * For 1.8 - 1.12.2, it does nothing.
     * @param blockData Block data
     * @param applyPhysics Applies physics or not
     */
    public void setTypeAndData(@NotNull BlockData blockData, boolean applyPhysics) {
        if (!Compatibility.checkBlockData()) return;
        location.getBlock().setType(blockData.getMaterial(), applyPhysics);
        Reflection.setBlockData(blockData, applyPhysics);
    }

    /**
     * Set type and data.<br />
     * For 1.13+, it does nothing.
     * @param material Material type
     * @param b Item data byte
     * @param applyPhysics Applies physics or not
     */
    @SuppressWarnings("deprecation")
    public void setTypeAndData(@NotNull Material material, byte b, boolean applyPhysics) {
        if (Compatibility.checkBlockData()) return;
        location.getBlock().setType(material, applyPhysics);
        location.getBlock().setData(b, applyPhysics);
    }

    @NotNull
    public Location getLocation() {
        return location;
    }

    @NotNull
    public Material getType() {
        return type;
    }

    public byte getData() {
        return data;
    }

    @Override
    public int hashCode() {
        return this.location.getBlockX() * this.location.getBlockY() * this.location.getBlockZ() * getType().ordinal() * getData();
    }
}
