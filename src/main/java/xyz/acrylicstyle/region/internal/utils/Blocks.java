package xyz.acrylicstyle.region.internal.utils;

import org.bukkit.Material;
import org.bukkit.World;
import xyz.acrylicstyle.craftbukkit.CraftUtils;
import xyz.acrylicstyle.minecraft.BlockPosition;
import xyz.acrylicstyle.region.api.block.BlockData;
import xyz.acrylicstyle.region.internal.nms.Chunk;
import xyz.acrylicstyle.tomeito_core.utils.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class Blocks {
    @SuppressWarnings("deprecation")
    public static void setBlock1_8_1_12_2(World world, int x, int y, int z, Material material, byte data) {
        int blockId = material.getId();
        try {
            Object o = CraftUtils.getHandle(world).getClass().getMethod("getChunkAt", int.class, int.class).invoke(CraftUtils.getHandle(world), x >> 4, z >> 4);
            new Chunk(o).sections[y >> 4].setType(x & 15, y & 15, z & 15, Objects.requireNonNull(getByCombinedId(blockId + (data << 12))));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static void setBlock1_13(World world, int x, int y, int z, BlockData blockData) {
        org.bukkit.Chunk chunk = world.getBlockAt(x, y, z).getChunk();
        Chunk.wrap(chunk).setType(new BlockPosition(x, y, z), blockData.getData(), false);
    }

    public static void setBlock(World world, int x, int y, int z, Material material, byte data, BlockData blockData) {
        if (Compatibility.checkNewPlayer_sendBlockChange()) {
            setBlock1_13(world, x, y, z, blockData);
        } else {
            setBlock1_8_1_12_2(world, x, y, z, material, data);
        }
    }

    public static Object getByCombinedId(int i) {
        try {
            return ReflectionUtil.getNMSClass("Block").getMethod("getByCombinedId", int.class).invoke(null, i);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
