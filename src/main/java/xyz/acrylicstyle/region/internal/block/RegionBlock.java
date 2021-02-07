package xyz.acrylicstyle.region.internal.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.region.api.block.Block;
import xyz.acrylicstyle.region.api.block.BlockData;
import xyz.acrylicstyle.region.api.reflector.org.bukkit.craftbukkit.block.CraftBlock;
import xyz.acrylicstyle.region.internal.utils.Compatibility;
import xyz.acrylicstyle.region.internal.utils.Reflection;

public class RegionBlock implements Block, Cloneable {
    @NotNull
    private final Location location;
    @NotNull
    private final Material type;
    private final byte data;
    @Nullable
    private final RegionBlockData blockData;

    public RegionBlock(@NotNull org.bukkit.block.Block block) {
        this.location = block.getLocation();
        this.type = block.getType();
        this.data = Reflection.getData(block);
        this.blockData = Reflection.getBlockData(block);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static RegionBlock wrap(@NotNull org.bukkit.block.Block block) {
        return new RegionBlock(block);
    }

    public RegionBlock(@NotNull Location location, @NotNull Material type, byte data, @Nullable RegionBlockData blockData) {
        this.location = location;
        this.type = type;
        this.data = data;
        this.blockData = blockData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public RegionBlockData getBlockData() {
        return blockData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public org.bukkit.block.Block getBukkitBlock() {
        return this.getLocation().getBlock();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBlockData(BlockData blockData) {
        if (!Compatibility.checkBlockData()) return;
        if (blockData == null) throw new AssertionError(); // BlockData class is available so blockData shouldn't be null.
        CraftBlock.getInstance(location.getBlock()).setBlockData(((RegionBlockData) blockData).getHandle());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTypeAndData(@NotNull Material material, byte b, BlockData blockData, boolean applyPhysics) {
        if (Compatibility.checkBlockData()) {
            setTypeAndData(material, blockData, applyPhysics);
        } else {
            setTypeAndData(material, b, applyPhysics);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTypeAndData(@NotNull Material material, @Nullable BlockData blockData, boolean applyPhysics) {
        if (!Compatibility.checkBlockData()) return;
        location.getBlock().setType(material, applyPhysics);
        if (blockData != null) Reflection.setBlockData((RegionBlockData) blockData, applyPhysics);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("deprecation")
    public void setTypeAndData(@NotNull Material material, byte b, boolean applyPhysics) {
        if (Compatibility.checkBlockData()) return;
        location.getBlock().setType(material, applyPhysics);
        location.getBlock().setData(b, applyPhysics);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public Location getLocation() {
        return location;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public Material getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte getData() {
        return data;
    }

    @Override
    public int hashCode() {
        return this.location.getBlockX() * this.location.getBlockY() * this.location.getBlockZ() * getType().ordinal() * getData();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RegionBlock clone() {
        try {
            return (RegionBlock) super.clone();
        } catch (CloneNotSupportedException e) {
            return new RegionBlock(location, type, data, blockData);
        }
    }
}
