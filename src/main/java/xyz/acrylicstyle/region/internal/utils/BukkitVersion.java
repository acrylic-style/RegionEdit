package xyz.acrylicstyle.region.internal.utils;

import org.jetbrains.annotations.NotNull;

public enum BukkitVersion {
    v1_8("MC 1.8 - 1.8.9"),
    v1_9("MC 1.9 - 1.12.2"),
    v1_13("MC 1.13 - 1.15.2"),
    // v1_14("MC 1.14 - 1.14.4"),
    // v1_15("MC 1.15+"),
    UNKNOWN("Unknown");

    @NotNull
    private String name;

    BukkitVersion(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public String getName() {
        return this.name;
    }
}
