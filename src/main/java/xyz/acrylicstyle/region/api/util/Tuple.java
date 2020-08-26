package xyz.acrylicstyle.region.api.util;

import org.bukkit.Location;
import org.bukkit.World;

import java.io.Serializable;
import java.util.function.Function;

public class Tuple<X, Y, Z> implements Serializable {
    private final X x;
    private final Y y;
    private final Z z;

    public Tuple(X x, Y y, Z z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public X getX() { return x; }

    public Y getY() { return y; }

    public Z getZ() { return z; }

    public <T> T convert(Function<Tuple<X, Y, Z>, T> function) { return function.apply(this); }

    public static class Converters {
        public static final Function<Tuple<Integer, Integer, Integer>, Location> TO_LOCATION = tuple -> new Location(null, tuple.x, tuple.y, tuple.z);

        public static Function<Tuple<Integer, Integer, Integer>, Location> toLocation(World world) {
            return tuple -> new Location(world, tuple.x, tuple.y, tuple.z);
        }
    }
}
