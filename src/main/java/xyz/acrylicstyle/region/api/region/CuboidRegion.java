package xyz.acrylicstyle.region.api.region;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.CollectionList;
import xyz.acrylicstyle.tomeito_api.TomeitoAPI;

/**
 * Cuboid region to specify pos1, and pos2 (cube)
 */
public class CuboidRegion implements RegionSelection, Cloneable {
    private final Location loc1;
    private final Location loc2;

    /**
     * Constructs new CuboidRegion.<br />
     * Locations must be y < 255 and y > 0.<br />
     * Location can be null, but you should not create instance that <b>both</b> locations are null.
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
    public Location getLocation() { return loc1; }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public CuboidRegion clone() {
        try {
            return (CuboidRegion) super.clone();
        } catch (CloneNotSupportedException e) { // impossible
            return new CuboidRegion(loc1, loc2);
        }
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public CollectionList<Location> getLocations() { return new CollectionList<>(loc1, loc2); }

    /**
     * Returns location #2. (pos2)
     * @return Location #2. (pos2)
     */
    public Location getLocation2() { return loc2; }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid() { return getLocation() != null && getLocation2() != null; }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInside(Location location) { return TomeitoAPI.inside(location, loc1, loc2); }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public CuboidRegion deepClone() { return new CuboidRegion(loc1.clone(), loc2.clone()); }

    @Override
    public long getSize() {
        int x1 = loc1.getX() > loc2.getX() ? (int) loc2.getX() : (int) loc1.getX();
        int y1 = loc1.getY() > loc2.getY() ? (int) loc2.getY() : (int) loc1.getY();
        int z1 = loc1.getZ() > loc2.getZ() ? (int) loc2.getZ() : (int) loc1.getZ();

        int x2 = ((int) loc1.getX()) == x1 ? (int) loc2.getX() : (int) loc1.getX();
        int y2 = ((int) loc1.getY()) == y1 ? (int) loc2.getY() : (int) loc1.getY();
        int z2 = ((int) loc1.getZ()) == z1 ? (int) loc2.getZ() : (int) loc1.getZ();
        long size = 0;
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    size++;
                }
            }
        }
        return size;
    }
}
