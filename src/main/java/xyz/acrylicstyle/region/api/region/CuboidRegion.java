package xyz.acrylicstyle.region.api.region;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.CollectionList;

/**
 * Cuboid region to specify pos1, and pos2 (cube)
 */
public class CuboidRegion implements RegionSelection, Cloneable {
    private final Location loc1;
    private final Location loc2;

    /**
     * Constructs new CuboidRegion.<br />
     * Locations must be y < 255 and y > 0.<br />
     * Location can be null, but if <b>both</b> locations was null, it fails.
     * @param loc1 Location (position) 1
     * @param loc2 Location (position) 2
     */
    public CuboidRegion(@Nullable Location loc1, @Nullable Location loc2) {
        if (loc1 != null && loc1.getY() > 255) loc1.setY(255);
        if (loc2 != null && loc2.getY() > 255) loc2.setY(255);
        if (loc1 != null && loc1.getY() < 0) loc1.setY(0);
        if (loc2 != null && loc2.getY() < 0) loc2.setY(0);
        this.loc1 = loc1;
        this.loc2 = loc2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Location getLocation() {
        return loc1;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public CuboidRegion clone() {
        try {
            return (CuboidRegion) super.clone();
        } catch (CloneNotSupportedException e) {
            return new CuboidRegion(loc1, loc2);
        }
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public CollectionList<Location> getLocations() {
        return new CollectionList<>(loc1, loc2);
    }

    /**
     * Returns location #2. (pos2)
     * @return Location #2. (pos2)
     */
    public Location getLocation2() {
        return loc2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid() {
        return getLocation() != null && getLocation2() != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInside(Location location) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        double x1 = loc1.getX();
        double y1 = loc1.getY();
        double z1 = loc1.getZ();
        double x2 = loc2.getX();
        double y2 = loc2.getY();
        double z2 = loc2.getZ();
        if ((x > x1) && (x < x2)) {
            if ((y > y1) && (y < y2)) {
                return (z > z1) && (z < z2);
            }
        }
        return false;
    }
}
