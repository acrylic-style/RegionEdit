package xyz.acrylicstyle.region.api.selection;

import xyz.acrylicstyle.region.api.region.CuboidRegion;
import xyz.acrylicstyle.region.api.region.RegionSelection;

public enum SelectionMode {
    CUBOID(CuboidRegion.class);

    private final Class<? extends RegionSelection> clazz;

    SelectionMode(Class<? extends RegionSelection> clazz) {
        this.clazz = clazz;
    }

    public Class<? extends RegionSelection> getClazz() {
        return clazz;
    }
}
