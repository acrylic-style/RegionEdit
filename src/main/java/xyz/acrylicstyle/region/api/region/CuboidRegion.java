package xyz.acrylicstyle.region.api.region;

import org.bukkit.Location;
import util.CollectionList;

/**
 * Cuboid region to specify pos1, and pos2 (cube)
 */
public class CuboidRegion implements RegionSelection {
    public final Location loc1;
    public final Location loc2;

    public CuboidRegion(Location loc1, Location loc2) {
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
