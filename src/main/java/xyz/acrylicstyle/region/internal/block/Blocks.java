package xyz.acrylicstyle.region.internal.block;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import util.BiBiConsumer;
import util.CollectionList;
import xyz.acrylicstyle.craftbukkit.v1_8_R3.util.CraftUtils;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.internal.nms.Chunk;
import xyz.acrylicstyle.region.internal.utils.Compatibility;
import xyz.acrylicstyle.region.internal.utils.Reflection;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Blocks {
    @SuppressWarnings("deprecation")
    public static void setBlock1_8_1_13_2(World world, int x, int y, int z, Material material, byte data) {
        int blockId = material.getId();
        try {
            Object o = CraftUtils.getHandle(world).getClass().getMethod("getChunkAt", int.class, int.class).invoke(CraftUtils.getHandle(world), x >> 4, z >> 4);
            new Chunk(o).sections[y >> 4].setType(x & 15, y & 15, z & 15, Objects.requireNonNull(getByCombinedId(blockId + (data << 12))));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
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

    public static void setBlock1_14(World world, int x, int y, int z, RegionBlockData blockData) {
        org.bukkit.Chunk chunk = world.getBlockAt(x, y, z).getChunk();
        try {
            Chunk.wrap(chunk).setType(Reflection.newRawBlockPosition(x, y, z), blockData.getHandle().getClass().getMethod("getState").invoke(blockData.getHandle()), false);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public static void setBlock(World world, int x, int y, int z, Material material, byte data, RegionBlockData blockData) {
        if (Compatibility.checkChunk_setType()) {
            setBlock1_14(world, x, y, z, blockData);
        } else {
            setBlock1_8_1_13_2(world, x, y, z, material, data);
        }
    }

    @SuppressWarnings({"DuplicatedCode", "unused"})
    public static void setBlocks(@NotNull CollectionList<Block> blocks, Material material, byte data, BiBiConsumer<Integer, Integer, Double> consumer) {
        double start = System.currentTimeMillis();
        Plugin plugin = RegionEdit.getInstance();
        AtomicInteger i0 = new AtomicInteger();
        AtomicInteger i = new AtomicInteger();
        final int taskId = RegionEditPlugin.taskId.getAndIncrement();
        blocks.map(RegionBlock::wrap).forEach(block -> {
            int x = block.getLocation().getBlockX();
            int y = block.getLocation().getBlockY();
            int z = block.getLocation().getBlockZ();
            World world = block.getLocation().getWorld();
            Blocks.setBlock(world, x, y, z, material, data, block.getBlockData());
            i0.incrementAndGet();
        });
        CollectionList<Map.Entry<Integer, Integer>> chunks = new CollectionList<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        while (true) {
                            if (i0.get() >= blocks.size()) {
                                blocks.forEach(b -> {
                                    for (Player p : Bukkit.getOnlinePlayers()) Reflection.sendBlockChange(p, b.getLocation(), material, data, Reflection.getBlockData(b));
                                    Reflection.notify(b.getWorld(), b, Reflection.newRawBlockPosition(b.getLocation().getBlockX(), b.getLocation().getBlockY(), b.getLocation().getBlockZ()));
                                    chunks.add(new AbstractMap.SimpleEntry<>(b.getChunk().getX(), b.getChunk().getZ()));
                                });
                                chunks.unique().forEach(e -> {
                                    Chunk chunk = Chunk.wrap(Objects.requireNonNull(blocks.first()).getWorld().getChunkAt(e.getKey(), e.getValue()));
                                    chunk.initLighting();
                                    for (Player p : Bukkit.getOnlinePlayers()) Reflection.sendChunk(p, chunk);
                                });
                                break;
                            }
                        }
                    }
                }.runTaskLaterAsynchronously(RegionEdit.getInstance(), 10);
                if (consumer != null) consumer.accept(blocks.size(), taskId, start);
            }
        }.runTaskLater(plugin, i.getAndIncrement());
    }
}
