package xyz.acrylicstyle.region.api.selection;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.region.api.region.CuboidRegion;
import xyz.acrylicstyle.region.api.region.RegionSelection;

public enum SelectionMode {
    CUBOID(CuboidRegion.class, "cuboid");

    @NotNull private final Class<? extends RegionSelection> clazz;
    @NotNull private final String shape;

    SelectionMode(@NotNull Class<? extends RegionSelection> clazz, @NotNull String shape) {
        this.clazz = clazz;
        this.shape = shape;
    }

    @NotNull
    public Class<? extends RegionSelection> getClazz() { return clazz; }

    @NotNull
    public String getShape() { return shape; }
}
