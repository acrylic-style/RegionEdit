package xyz.acrylicstyle.region.api.region;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import util.DeepCloneable;

public interface Region extends Cloneable, DeepCloneable {
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

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    Region deepClone();
}
