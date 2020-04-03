package xyz.acrylicstyle.region.internal.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import util.ReflectionHelper;
import xyz.acrylicstyle.region.api.block.BlockData;
import xyz.acrylicstyle.region.internal.utils.Reflection;
import xyz.acrylicstyle.tomeito_core.utils.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class RegionBlockData implements BlockData {
    @NotNull
    private final Object data;

    public RegionBlockData(@NotNull Object o) {
        this.data = o;
    }

    public RegionBlockData(@NotNull Block block) {
        this.data = Objects.requireNonNull(Reflection.getBlockData(block));
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
        return RegionBlock.wrap((Block) data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public Block getBukkitBlock() {
        return (Block) data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String getAsString() {
        try {
            return (String) ReflectionHelper.invokeMethod(ReflectionUtil.getOBCClass("CraftBlockData"), data, "getAsString");
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String getAsString(boolean paramBoolean) {
        try {
            return (String) ReflectionHelper.invokeMethod(ReflectionUtil.getOBCClass("CraftBlockData"), data, "getAsString", paramBoolean);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public Material getMaterial() {
        try {
            return (Material) ReflectionHelper.invokeMethod(ReflectionUtil.getOBCClass("CraftBlockData"), data, "getMaterial");
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public Object getState() {
        try {
            return ReflectionHelper.invokeMethod(ReflectionUtil.getOBCClass("CraftBlockData"), data, "getState");
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public BlockData merge(@NotNull BlockData paramBlockData) {
        try {
            return new RegionBlockData(ReflectionHelper.invokeMethod(
                    ReflectionUtil.getOBCClass("CraftBlockData"),
                    data,
                    "merge",
                    ((RegionBlockData) paramBlockData).getHandle())
            );
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(@NotNull BlockData paramBlockData) {
        try {
            return (boolean) ReflectionHelper.invokeMethod(
                    ReflectionUtil.getOBCClass("CraftBlockData"),
                    data,
                    "matches",
                    ((RegionBlockData) paramBlockData).getHandle()
            );
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public Object getHandle() {
        return data;
    }
}
