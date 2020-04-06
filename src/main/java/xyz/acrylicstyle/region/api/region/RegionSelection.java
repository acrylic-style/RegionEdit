package xyz.acrylicstyle.region.api.region;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.CollectionList;

public interface RegionSelection extends Region {
    /**
     * Returns root location. (pos1)
     * @return Root Location.
     */
    @Nullable Location getLocation();

    /**
     * Returns all selected locations.
     * @return All selected locations.
     */
    @NotNull CollectionList<Location> getLocations();

    /**
     * Returns if it's valid selection.
     * @return True if it's valid selection, false otherwise.
     */
    boolean isValid();

    /**
     * Checks if location is in region.
     * @param location An any location
     */
    boolean isInside(Location location);
}
