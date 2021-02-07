package xyz.acrylicstyle.region.internal.block;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import util.CollectionList;
import util.CollectionSet;
import util.ICollectionList;
import util.reflect.Ref;
import util.reflector.Reflector;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.reflector.net.minecraft.server.IBlockData;
import xyz.acrylicstyle.region.internal.nms.Chunk;
import xyz.acrylicstyle.region.internal.utils.Compatibility;
import xyz.acrylicstyle.region.internal.utils.Reflection;
import xyz.acrylicstyle.tomeito_api.TomeitoAPI;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class BlockUtil {
    public static void setBlockOld(World world, int x, int y, int z, int blockId, byte data) {
        setBlockOld(world, x, y, z, xyz.acrylicstyle.region.api.reflector.net.minecraft.server.Block.STATIC.getByCombinedId(blockId + (data << 12)));
    }

    public static void setBlockOld(World world, int x, int y, int z, IBlockData iBlockData) {
        Chunk.getInstance(world.getChunkAt(x >> 4, z >> 4)).sections[y >> 4].setType(x, y, z, Reflector.getUnproxiedInstance(iBlockData).getOrThrow());
    }

    @Nullable
    @SuppressWarnings("deprecation")
    public static Material getMaterialById(int i) {
        if (Compatibility.checkMaterial_getMaterial_I()) {
            return Material.getMaterial(i);
        } else {
            IBlockData data = xyz.acrylicstyle.region.api.reflector.net.minecraft.server.Block.STATIC.getByCombinedId(i);
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
                setBlockOld(world, x, y, z, xyz.acrylicstyle.region.api.reflector.net.minecraft.server.Block.STATIC.getCombinedId(blockData.getState()), (byte) 0);
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
            Reflection.notify(world, loc.getBlock(), Reflection.newRawBlockPosition(x, y, z));
            chunks.add(new AbstractMap.SimpleEntry<>(x >> 4, z >> 4));
            chunks.unique().forEach(e -> {
                org.bukkit.Chunk bc = world.getChunkAt(e.getKey(), e.getValue());
                Chunk chunk = Chunk.getInstance(bc);
                chunk.initLighting();
                for (Player p : getVisiblePlayers(bc))
                    Reflection.sendBlockChange(p, loc, material, data, blockData);
            });
        });
    }

    public static void sendBlockChanges(ICollectionList<Block> blocks) {
        Bukkit.getScheduler().runTaskAsynchronously(RegionEdit.getInstance(), () -> {
            CollectionList<Map.Entry<Integer, Integer>> chunks = new CollectionList<>();
            blocks.forEach(b -> {
                Reflection.notify(b.getWorld(), b, Reflection.newRawBlockPosition(b.getLocation().getBlockX(), b.getLocation().getBlockY(), b.getLocation().getBlockZ()));
                chunks.add(new AbstractMap.SimpleEntry<>(b.getChunk().getX(), b.getChunk().getZ()));
            });
            chunks.unique().forEach(entry -> {
                org.bukkit.Chunk bc = Objects.requireNonNull(blocks.first()).getWorld().getChunkAt(entry.getKey(), entry.getValue());
                Chunk chunk = Chunk.getInstance(bc);
                chunk.initLighting();
                Reflection.sendBlockChangesWithLocations(getVisiblePlayers(bc), blocks.map((Function<Block, Location>) Block::getLocation), chunk);
            });
        });
    }

    @SuppressWarnings("RedundantIfStatement")
    public static Set<Player> getVisiblePlayers(org.bukkit.Chunk chunk) {
        return new CollectionSet<>(TomeitoAPI.getOnlinePlayers()).filter(p -> {
            org.bukkit.Chunk c = p.getLocation().getChunk();
            if (!c.getWorld().getUID().equals(chunk.getWorld().getUID())) return false;
            if (Math.abs(c.getX() - chunk.getX()) > Bukkit.getViewDistance()) return false;
            if (Math.abs(c.getZ() - chunk.getZ()) > Bukkit.getViewDistance()) return false;
            return true;
        });
    }
}
