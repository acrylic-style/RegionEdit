package xyz.acrylicstyle.region.api.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.region.api.exception.RegionSelectorException;
import xyz.acrylicstyle.region.api.region.CuboidRegion;
import xyz.acrylicstyle.region.api.region.RegionSelection;
import xyz.acrylicstyle.region.api.selection.SelectionMode;

import java.util.UUID;

public interface UserSession {
    /**
     * Returns user's UUID.
     * @return User's UUID.
     */
    @NotNull
    UUID getUniqueId();

    /**
     * Returns user's current region selection.
     * @return User's current region selection.
     */
    @Nullable
    RegionSelection getRegionSelection();

    /**
     * Set user's selected region.
     * @param regionSelection New region selection.
     * @throws RegionSelectorException When RegionSelection isn't compatible with current selection mode.
     */
    void setRegionSelection(@NotNull RegionSelection regionSelection) throws RegionSelectorException;

    /**
     * Returns user's selection mode.
     * @return User's selection mode. Cuboid by default.
     */
    @NotNull
    SelectionMode getSelectionMode();

    /**
     * Set user's selection mode.
     * @param selectionMode New selection mode.
     */
    void setSelectionMode(@NotNull SelectionMode selectionMode);

    /**
     * Returns user's cuboid region selection.
     * @return User's cuboid region selection.
     * @throws RegionSelectorException When current region selection isn't instance of CuboidRegion.
     */
    @NotNull
    CuboidRegion getCuboidRegion() throws RegionSelectorException;
}
