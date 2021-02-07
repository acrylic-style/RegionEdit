package xyz.acrylicstyle.region.internal.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.region.api.block.BlockData;
import xyz.acrylicstyle.region.api.reflector.net.minecraft.server.IBlockData;
import xyz.acrylicstyle.region.api.reflector.org.bukkit.craftbukkit.block.CraftBlock;
import xyz.acrylicstyle.region.api.reflector.org.bukkit.craftbukkit.v1_13.block.data.CraftBlockData;

import java.util.Objects;

public class RegionBlockData implements BlockData {
    @NotNull
    private final Block block;
    @NotNull
    private final CraftBlockData data;

    public RegionBlockData(@NotNull Block block, @NotNull CraftBlockData o) {
        this.block = block;
        this.data = o;
    }

    public RegionBlockData(@NotNull Block block) {
        this.block = block;
        this.data = Objects.requireNonNull(CraftBlock.getInstance(block).getBlockData());
    }

    @NotNull
    @Contract("_ -> new")
    public static RegionBlockData wrap(@NotNull Block block) {
        return new RegionBlockData(block);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public xyz.acrylicstyle.region.api.block.Block getBlock() {
        return RegionBlock.wrap(block);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public Block getBukkitBlock() {
        return block;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String getAsString() {
        return data.getAsString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String getAsString(boolean flag) {
        return data.getAsString(flag);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public Material getMaterial() {
        return data.getMaterial();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public IBlockData getState() {
        return data.getState();
    }

    @Override
    @NotNull
    public BlockData merge(@NotNull BlockData blockData) {
        return new RegionBlockData(block, data.merge(((RegionBlockData) blockData).getHandle()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(@NotNull BlockData blockData) {
        return data.matches(((RegionBlockData) blockData).getHandle());
    }

    @NotNull
    public CraftBlockData getHandle() {
        return data;
    }
}
