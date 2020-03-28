package xyz.acrylicstyle.region.api.region;

import org.bukkit.Location;
import util.CollectionList;

public interface RegionSelection extends Region {
    /**
     * Returns root location. (pos1)
     * @return Root Location.
     */
    Location getLocation();

    /**
     * Returns all selected locations.
     * @return All selected locations.
     */
    CollectionList<Location> getLocations();

    /**
     * Returns if it's valid selection.
     * @return True if it's valid selection, false otherwise.
     */
    boolean isValid();
}
