package xyz.acrylicstyle.region.api.selection;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.region.api.region.CuboidRegion;
import xyz.acrylicstyle.region.api.region.RegionSelection;

public enum SelectionMode {
    CUBOID(CuboidRegion.class);

    @NotNull
    private final Class<? extends RegionSelection> clazz;

    SelectionMode(@NotNull Class<? extends RegionSelection> clazz) { this.clazz = clazz; }

    @NotNull
    public Class<? extends RegionSelection> getClazz() { return clazz; }
}
