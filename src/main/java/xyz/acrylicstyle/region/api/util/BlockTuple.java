package xyz.acrylicstyle.region.api.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

public class BlockTuple<X, Y, Z> implements Serializable {
    protected final UUID uid;
    protected final X x;
    protected final Y y;
    protected final Z z;

    public BlockTuple(World world, X x, Y y, Z z) {
        this.uid = world.getUID();
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public World getWorld() { return Bukkit.getWorld(uid); }

    public X getX() { return x; }

    public Y getY() { return y; }

    public Z getZ() { return z; }

    public <T> T convert(Function<BlockTuple<X, Y, Z>, T> function) { return function.apply(this); }

    public static class Converters {
        public static final Function<Location, BlockTuple<Integer, Integer, Integer>> FROM_LOCATION = location -> new BlockTuple<>(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());

        public static final Function<BlockTuple<Integer, Integer, Integer>, Location> TO_LOCATION = tuple -> new Location(tuple.getWorld(), tuple.x, tuple.y, tuple.z);

        public static final Function<BlockTuple<Integer, Integer, Integer>, Block> TO_BLOCK = tuple -> TO_LOCATION.apply(tuple).getBlock();

        public static Function<BlockTuple<Integer, Integer, Integer>, Location> toLocation(World world) {
            return tuple -> new Location(world, tuple.x, tuple.y, tuple.z);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockTuple<?, ?, ?> that = (BlockTuple<?, ?, ?>) o;
        return Objects.equals(uid, that.uid) && Objects.equals(x, that.x) && Objects.equals(y, that.y) && Objects.equals(z, that.z);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, x, y, z);
    }

    @Override
    public String toString() {
        return "BlockTuple{" +
                "uid=" + uid +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
