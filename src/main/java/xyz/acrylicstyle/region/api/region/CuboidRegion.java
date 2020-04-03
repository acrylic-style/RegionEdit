package xyz.acrylicstyle.region.api.region;

import org.bukkit.Location;
import util.CollectionList;

/**
 * Cuboid region to specify pos1, and pos2 (cube)
 */
public class CuboidRegion implements RegionSelection, Cloneable {
    public final Location loc1;
    public final Location loc2;

    public CuboidRegion(Location loc1, Location loc2) {
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

    @Override
    public boolean isValid() {
        return getLocation() != null && getLocation2() != null;
    }
}
