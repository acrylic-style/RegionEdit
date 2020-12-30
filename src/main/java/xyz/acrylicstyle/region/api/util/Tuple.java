package xyz.acrylicstyle.region.api.util;

import org.bukkit.Location;
import org.bukkit.World;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

public final class Tuple<X, Y, Z> implements Serializable {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple<?, ?, ?> tuple = (Tuple<?, ?, ?>) o;
        if (!Objects.equals(x, tuple.x)) return false;
        if (!Objects.equals(y, tuple.y)) return false;
        return Objects.equals(z, tuple.z);
    }

    @Override
    public String toString() {
        return "Tuple{" + "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    @Override
    public int hashCode() {
        int result = x != null ? x.hashCode() : 0;
        result = 31 * result + (y != null ? y.hashCode() : 0);
        result = 31 * result + (z != null ? z.hashCode() : 0);
        return result;
    }

    public static class Converters {
        public static final Function<Tuple<Integer, Integer, Integer>, Location> TO_LOCATION = tuple -> new Location(null, tuple.x, tuple.y, tuple.z);

        public static Function<Tuple<Integer, Integer, Integer>, Location> toLocation(World world) {
            return tuple -> new Location(world, tuple.x, tuple.y, tuple.z);
        }
    }
}
