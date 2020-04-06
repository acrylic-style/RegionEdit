package xyz.acrylicstyle.region.api.region;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface Region extends Cloneable {
    /**
     * Get Location.
     * @return Location.
     */
    Location getLocation();

    /**
     * Clones this region into another region.
     * @return Shallow copy of the region
     */
    @NotNull
    Region clone();
}
