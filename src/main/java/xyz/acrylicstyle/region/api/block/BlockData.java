package xyz.acrylicstyle.region.api.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import util.ReflectionHelper;
import xyz.acrylicstyle.region.internal.utils.Reflection;
import xyz.acrylicstyle.tomeito_core.utils.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class BlockData {
    @NotNull
    private final Object data;

    public BlockData(@NotNull Object o) {
        this.data = o;
    }

    public BlockData(@NotNull Block block) {
        this.data = Objects.requireNonNull(Reflection.getBlockData(block));
    }

    @NotNull
    @Contract("_ -> new")
    public static BlockData wrap(@NotNull Block block) {
        return new BlockData(block);
    }

    public xyz.acrylicstyle.region.api.block.Block getBlock() {
        return xyz.acrylicstyle.region.api.block.Block.wrap((Block) data);
    }

    public Block getBukkitBlock() {
        return (Block) data;
    }

    @NotNull
    public String getAsString() {
        try {
            return (String) ReflectionHelper.invokeMethod(ReflectionUtil.getOBCClass("CraftBlockData"), data, "getAsString");
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public String getAsString(boolean paramBoolean) {
        try {
            return (String) ReflectionHelper.invokeMethod(ReflectionUtil.getOBCClass("CraftBlockData"), data, "getAsString", paramBoolean);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public Material getMaterial() {
        try {
            return (Material) ReflectionHelper.invokeMethod(ReflectionUtil.getOBCClass("CraftBlockData"), data, "getMaterial");
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public Object getState() {
        try {
            return ReflectionHelper.invokeMethod(ReflectionUtil.getOBCClass("CraftBlockData"), data, "getState");
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public BlockData merge(@NotNull BlockData paramBlockData) {
        try {
            return new BlockData(ReflectionHelper.invokeMethod(ReflectionUtil.getOBCClass("CraftBlockData"), data, "merge", paramBlockData.getData()));
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean matches(@NotNull BlockData paramBlockData) {
        try {
            return (boolean) ReflectionHelper.invokeMethod(ReflectionUtil.getOBCClass("CraftBlockData"), data, "matches", paramBlockData.getData());
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public Object getData() {
        return data;
    }
}
