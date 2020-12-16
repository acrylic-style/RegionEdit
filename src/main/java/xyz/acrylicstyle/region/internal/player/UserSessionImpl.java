package xyz.acrylicstyle.region.internal.player;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.CollectionList;
import util.ICollectionList;
import util.Validate;
import xyz.acrylicstyle.mcutil.lang.MCVersion;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.block.state.BlockState;
import xyz.acrylicstyle.region.api.exception.RegionEditException;
import xyz.acrylicstyle.region.api.player.SuperPickaxeMode;
import xyz.acrylicstyle.region.api.player.UserSession;
import xyz.acrylicstyle.region.api.region.CuboidRegion;
import xyz.acrylicstyle.region.api.region.RegionSelection;
import xyz.acrylicstyle.region.api.selection.SelectionMode;
import xyz.acrylicstyle.region.internal.RegionEditImpl;
import xyz.acrylicstyle.shared.NMSAPI;
import xyz.acrylicstyle.shared.OBCAPI;
import xyz.acrylicstyle.tomeito_api.TomeitoAPI;
import xyz.acrylicstyle.tomeito_api.utils.Log;

import java.util.Objects;
import java.util.UUID;

public final class UserSessionImpl implements UserSession {
    private final RegionEditImpl plugin;
    private final UUID uuid;

    public UserSessionImpl(@NotNull RegionEditImpl plugin, @NotNull UUID uuid) {
        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(uuid, "uuid cannot be null");
        this.plugin = plugin;
        this.uuid = uuid;
    }

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

    private boolean cuiSupport = false;

    @Override
    public void handleCUIInitialization() {
        handleCUIInitialization(false);
    }

    public void handleCUIInitialization(boolean log) {
        if (log) Log.info("Enabling CUI for " + uuid.toString());
        this.cuiSupport = true;
    }

    @Override
    public void sendCUIEvent() {
        if (!cuiSupport) return;
        Player player = getPlayer();
        if (player == null) return;
        if (getSelectionMode() == SelectionMode.CUBOID) {
            sendCuboidRegion();
        }
    }

    private String getPoint(int id, Location loc, long size) {
        return "p|" + id + "|" + loc.getBlockX() + "|" + loc.getBlockY() + "|" + loc.getBlockZ() + "|" + size;
    }

    private void sendCuboidRegion() {
        if (!cuiSupport) return;
        Player player = getPlayer();
        if (player == null) return;
        CuboidRegion region = Objects.requireNonNull(getCuboidRegion());
        player.sendPluginMessage(plugin, getCUIChannel(), "s|cuboid".getBytes());
        if (region.getLocation() != null) player.sendPluginMessage(plugin, getCUIChannel(), getPoint(0, region.getLocation(), 1).getBytes());
        if (region.getLocation2() != null) player.sendPluginMessage(plugin, getCUIChannel(), getPoint(1, region.getLocation2(), region.getLocation() == null ? 1 : region.size()).getBytes());
    }

    @Override
    public @Nullable Player getPlayer() { return Bukkit.getPlayer(uuid); }

    @Override
    public boolean hasCUISupport() { return this.cuiSupport; }

    @Override
    public void setCUISupport(boolean flag) { this.cuiSupport = flag; }

    private int protocolVersion = -1;

    @Override
    public int getProtocolVersion() {
        if (protocolVersion != -1) return protocolVersion;
        Player player = getPlayer();
        if (player == null) return protocolVersion;
        protocolVersion = TomeitoAPI.getProtocolVersion(player).complete();
        return protocolVersion;
    }

    @Override
    public @NotNull MCVersion getMinecraftVersion() {
        CollectionList<MCVersion> list = ICollectionList.asList(MCVersion.getByProtocolVersion(getProtocolVersion()));
        return list.filter(v -> !v.isSnapshot()).size() == 0 // if non-snapshot version wasn't found
                ? Objects.requireNonNull(list.first()) // return the last version anyway
                : Objects.requireNonNull(list.filter(v -> !v.isSnapshot()).first()); // or return non-snapshot version if any
    }

    @Override
    public @NotNull String getCUIChannel() {
        return getMinecraftVersion().isModern() || getMinecraftVersion() == MCVersion.UNKNOWN ? RegionEditPlugin.CUI : RegionEditPlugin.CUI_LEGACY;
    }

    private ICollectionList<BlockState> clipboard = null;

    @Override
    public ICollectionList<BlockState> getClipboard() { return clipboard; }

    @Override
    public void setClipboard(ICollectionList<BlockState> blocks) { this.clipboard = blocks; }
}
