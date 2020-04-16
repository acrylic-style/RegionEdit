package xyz.acrylicstyle.region.internal.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.exception.RegionEditException;
import xyz.acrylicstyle.region.api.player.UserSession;
import xyz.acrylicstyle.region.api.region.CuboidRegion;
import xyz.acrylicstyle.region.api.region.RegionSelection;
import xyz.acrylicstyle.region.api.selection.SelectionMode;

import java.util.UUID;

public class UserSessionImpl implements UserSession {
    private final UUID uuid;

    public UserSessionImpl(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    @NotNull
    public UUID getUniqueId() {
        return uuid;
    }

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
    public boolean isFastMode() {
        return fastMode;
    }

    @Override
    public void setFastMode(boolean flag) {
        this.fastMode = flag;
    }

    private boolean superPickaxe = false;

    @Override
    public boolean isSuperPickaxeEnabled() {
        return superPickaxe;
    }

    @Override
    public void setSuperPickaxe(boolean flag) {
        this.superPickaxe = flag;
    }
}
