package xyz.acrylicstyle.region.internal.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.exception.RegionEditException;
import xyz.acrylicstyle.region.api.player.SuperPickaxeMode;
import xyz.acrylicstyle.region.api.player.UserSession;
import xyz.acrylicstyle.region.api.region.CuboidRegion;
import xyz.acrylicstyle.region.api.region.RegionSelection;
import xyz.acrylicstyle.region.api.selection.SelectionMode;

import java.util.UUID;

public class UserSessionImpl implements UserSession {
    private final UUID uuid;

    public UserSessionImpl(UUID uuid) { this.uuid = uuid; }

    @Override
    @NotNull
    public UUID getUniqueId() { return uuid; }

    @Override
    @Nullable
    public RegionSelection getRegionSelection() { return RegionEditPlugin.regionSelection.get(uuid); }

    @Override
    public void setRegionSelection(RegionSelection regionSelection) throws RegionEditException {
        if (regionSelection == null) {
            RegionEditPlugin.regionSelection.remove(uuid);
            return;
        }
        if (!regionSelection.getClass().isAssignableFrom(getSelectionMode().getClazz())) throw new RegionEditException("RegionSelection isn't compatible with current selection mode!");
        RegionEditPlugin.regionSelection.add(uuid, regionSelection);
    }

    @Override
    @NotNull
    public SelectionMode getSelectionMode() { return RegionEditPlugin.selectionMode.getOrDefault(uuid, SelectionMode.CUBOID); }

    @Override
    public void setSelectionMode(@NotNull SelectionMode selectionMode) {
        RegionEditPlugin.regionSelection.remove(uuid);
        RegionEditPlugin.selectionMode.add(uuid, selectionMode);
    }

    @Override
    @NotNull
    public CuboidRegion getCuboidRegion() throws RegionEditException {
        RegionSelection region = getRegionSelection();
        if (region == null) return new CuboidRegion(null, null);
        if (!(region instanceof CuboidRegion)) throw new RegionEditException("Current region selection isn't instance of CuboidRegion!");
        return (CuboidRegion) region;
    }

    private boolean fastMode = true;

    @Override
    public boolean isFastMode() { return fastMode; }

    @Override
    public void setFastMode(boolean flag) { this.fastMode = flag; }

    @NotNull
    private SuperPickaxeMode mode = SuperPickaxeMode.OFF;

    @Override
    public @NotNull SuperPickaxeMode getSuperPickaxeMode() { return this.mode; }

    @Override
    public void setSuperPickaxeMode(@NotNull SuperPickaxeMode mode) { this.mode = mode; }

    private int superPickaxeRadius = 1;

    @Override
    public int getSuperPickaxeRadius() { return this.superPickaxeRadius; }

    @Override
    public void setSuperPickaxeRadius(int radius) throws RegionEditException {
        if (radius < 1) throw new RegionEditException("cannot set radius lower than 1");
        if (radius > 10) throw new RegionEditException("cannot set radius higher than 10");
        this.superPickaxeRadius = radius;
    }

    private boolean drawSelection = false;

    @Override
    public boolean isDrawSelection() { return this.drawSelection; }

    @Override
    public void setDrawSelection(boolean flag) { this.drawSelection = flag; }
}
