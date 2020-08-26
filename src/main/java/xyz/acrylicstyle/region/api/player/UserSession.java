package xyz.acrylicstyle.region.api.player;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import util.ICollectionList;
import xyz.acrylicstyle.mcutil.lang.MCVersion;
import xyz.acrylicstyle.region.api.block.state.BlockState;
import xyz.acrylicstyle.region.api.exception.RegionEditException;
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
     * @param regionSelection New region selection. Set to null to remove.
     * @throws RegionEditException When RegionSelection isn't compatible with current selection mode.
     */
    void setRegionSelection(RegionSelection regionSelection) throws RegionEditException;

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
     * @throws RegionEditException When current region selection isn't instance of CuboidRegion.
     */
    @NotNull
    CuboidRegion getCuboidRegion() throws RegionEditException;

    /**
     * Returns whatever if player enabled a fast mode or not.
     * @return Fast mode enabled or not
     */
    boolean isFastMode();

    /**
     * Set fast mode to specified flag.
     * @param flag Fast Mode flag
     */
    void setFastMode(boolean flag);

    @NotNull
    SuperPickaxeMode getSuperPickaxeMode();

    /**
     * Set super pickaxe mode to specified flag.
     * @param mode super pickaxe mode
     */
    void setSuperPickaxeMode(@NotNull SuperPickaxeMode mode);

    @Range(from = 1, to = Integer.MAX_VALUE)
    int getSuperPickaxeRadius();

    /**
     * Set super pickaxe radius.
     * @param radius the radius
     * @throws RegionEditException when radius is <1 or >10
     */
    void setSuperPickaxeRadius(int radius) throws RegionEditException;

    boolean isDrawSelection();

    void setDrawSelection(boolean flag);

    void handleCUIInitialization();

    void sendCUIEvent();

    /**
     * Get the player who holds this session.
     * @return the player, null if logged out
     */
    @Nullable
    Player getPlayer();

    boolean hasCUISupport();

    void setCUISupport(boolean flag);

    /**
     * Get protocol version of this player, if offline, it returns the latest cached value.
     * @return the (cached) protocol version
     */
    int getProtocolVersion();

    /**
     * Try to get minecraft version of this player.<br />
     * The value may be inaccurate if multiple versions
     * has the same protocol version.<br />
     * If the player is offline, it returns the latest cached value.
     * @return the minecraft version
     */
    @NotNull
    MCVersion getMinecraftVersion();

    @NotNull
    String getCUIChannel();

    ICollectionList<BlockState> getClipboard();

    void setClipboard(ICollectionList<BlockState> blocks);
}
