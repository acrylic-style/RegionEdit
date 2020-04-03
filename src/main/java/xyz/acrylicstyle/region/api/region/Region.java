package xyz.acrylicstyle.region.api.region;

import org.bukkit.Location;

public interface Region extends Cloneable {
    /**
     * Get Location.
     * @return Location.
     */
    Location getLocation();

    Region clone();
}
