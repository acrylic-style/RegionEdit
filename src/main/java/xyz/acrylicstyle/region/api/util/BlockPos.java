package xyz.acrylicstyle.region.api.util;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.io.Serializable;

public final class BlockPos extends BlockTuple<Integer, Integer, Integer> implements Serializable {
    public BlockPos(World world, Tuple<Integer, Integer, Integer> tuple) {
        super(world, tuple.getX(), tuple.getY(), tuple.getZ());
    }

    public BlockPos(Location location) {
        super(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public BlockPos(World world, int x, int y, int z) {
        super(world, x, y, z);
    }

    public Location getLocation() { return new Location(getWorld(), x, y, z); }

    public Block getBlock() {
        return getLocation().getBlock();
    }

    public int getBlockX() { return x; }

    public int getBlockY() { return y; }

    public int getBlockZ() { return z; }

    public Chunk getChunk() { return getLocation().getChunk(); }
}
