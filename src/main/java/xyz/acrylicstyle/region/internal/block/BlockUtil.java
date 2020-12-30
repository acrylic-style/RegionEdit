package xyz.acrylicstyle.region.internal.block;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.BiBiConsumer;
import util.CollectionList;
import util.ICollectionList;
import util.reflect.Ref;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.block.BlockData;
import xyz.acrylicstyle.region.internal.nms.Chunk;
import xyz.acrylicstyle.region.internal.utils.Compatibility;
import xyz.acrylicstyle.region.internal.utils.Reflection;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class BlockUtil {
    public static void setBlockOld(World world, int x, int y, int z, int blockId, byte data) {
        setBlockOld(world, x, y, z, getByCombinedId(blockId + (data << 12)));
    }

    public static void setBlockOld(World world, int x, int y, int z, Object iBlockData) {
        Chunk.getInstance(world.getChunkAt(x >> 4, z >> 4)).sections[y >> 4].setType(x, y, z, iBlockData);
    }

    public static int getCombinedId(@NotNull BlockData blockData) {
        return (int) Ref.forName(ReflectionUtil.getNMSPackage() + ".Block")
                .getMethod("getCombinedId", Ref.forName(ReflectionUtil.getNMSPackage() + ".IBlockData").getClazz())
                .invoke(null, blockData.getState());
    }

    public static int getCombinedId(@NotNull Object obj) {
        return (int) Ref.forName(ReflectionUtil.getNMSPackage() + ".Block")
                .getMethod("getCombinedId", Ref.forName(ReflectionUtil.getNMSPackage() + ".IBlockData").getClazz())
                .invoke(null, obj);
    }

    public static Object getByCombinedId(int i) {
        try {
            return ReflectionUtil.getNMSClass("Block").getMethod("getByCombinedId", int.class).invoke(null, i);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    @SuppressWarnings("deprecation")
    public static Material getMaterialById(int i) {
        if (Compatibility.checkMaterial_getMaterial_I()) {
            return Material.getMaterial(i);
        } else {
            Object data = getByCombinedId(i);
            if (data == null) return null;
            return getMaterialFromIBlockData(data);
        }
    }

    @SuppressWarnings("deprecation")
    public static Material getMaterialFromIBlockData(Object iBlockData) {
        Object craftBlockData = Ref.getClass(iBlockData.getClass()).getMethod("createCraftBlockData").invokeObj(iBlockData);
        return (Material) Ref.getClass(craftBlockData.getClass()).getMethod("getMaterial").invokeObj(craftBlockData);
    }

    public static void setBlockNew(World world, int x, int y, int z, @Nullable RegionBlockData blockData) {
        org.bukkit.Chunk chunk = world.getBlockAt(x, y, z).getChunk();
        try {
            Chunk.getInstance(chunk).setType(Reflection.newRawBlockPosition(x, y, z), blockData == null ? null : blockData.getHandle().getClass().getMethod("getState").invoke(blockData.getHandle()), false);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public static void setBlock(World world, int x, int y, int z, Material material, byte data, @Nullable RegionBlockData blockData) {
        if (Compatibility.checkChunkSection()) {
            if (Compatibility.checkBlockData() && blockData != null) {
                setBlockOld(world, x, y, z, getCombinedId(blockData), (byte) 0);
            } else {
                setBlockOld(world, x, y, z, material.getId(), data);
            }
        } else {
            setBlockNew(world, x, y, z, blockData);
        }
    }

    public static void setBlockSendBlockChange(World world, int x, int y, int z, Material material, byte data, RegionBlockData blockData) {
        setBlock(world, x, y, z, material, data, blockData);
        CollectionList<Map.Entry<Integer, Integer>> chunks = new CollectionList<>();
        Location loc = new Location(world, x, y, z);
        Bukkit.getScheduler().runTaskAsynchronously(RegionEdit.getInstance(), () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                Reflection.sendBlockChange(p, loc, material, data, blockData);
            }
            Reflection.notify(world, loc.getBlock(), Reflection.newRawBlockPosition(x, y, z));
            chunks.add(new AbstractMap.SimpleEntry<>(x >> 4, z >> 4));
            chunks.unique().forEach(e -> {
                Chunk chunk = Chunk.getInstance(world.getChunkAt(e.getKey(), e.getValue()));
                chunk.initLighting();
                for (Player p : Bukkit.getOnlinePlayers()) Reflection.sendChunk(p, chunk);
            });
        });
    }

    public static void sendBlockChanges(ICollectionList<Block> blocks, Material type, byte data) {
        Bukkit.getScheduler().runTaskAsynchronously(RegionEdit.getInstance(), () -> {
            CollectionList<Map.Entry<Integer, Integer>> chunks = new CollectionList<>();
            blocks.forEach(b -> {
                for (Player p : Bukkit.getOnlinePlayers())
                    Reflection.sendBlockChange(p, b.getLocation(), type, data, Reflection.getBlockData(b));
                Reflection.notify(b.getWorld(), b, Reflection.newRawBlockPosition(b.getLocation().getBlockX(), b.getLocation().getBlockY(), b.getLocation().getBlockZ()));
                chunks.add(new AbstractMap.SimpleEntry<>(b.getChunk().getX(), b.getChunk().getZ()));
            });
            chunks.unique().forEach(entry -> {
                Chunk chunk = Chunk.getInstance(Objects.requireNonNull(blocks.first()).getWorld().getChunkAt(entry.getKey(), entry.getValue()));
                chunk.initLighting();
                for (Player p : Bukkit.getOnlinePlayers()) Reflection.sendChunk(p, chunk);
            });
        });
    }

    @SuppressWarnings({"DuplicatedCode"}) // unused
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
            BlockUtil.setBlock(world, x, y, z, material, data, block.getBlockData());
            i0.incrementAndGet();
        });
        new BukkitRunnable() {
            @Override
            public void run() {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        while (true) {
                            if (i0.get() >= blocks.size()) {
                                sendBlockChanges(blocks, material, data);
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
