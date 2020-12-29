package xyz.acrylicstyle.region.internal.utils;

import org.jetbrains.annotations.NotNull;
import util.CollectionList;
import util.ICollectionList;

public enum BukkitVersion {
    v1_8("MC 1.8 - 1.8.9"),
    v1_9("MC 1.9 - 1.12.2"),
    v1_13("MC 1.13 - 1.13.1"),
    v1_13_2("MC 1.13.2", v1_13),
    v1_14("MC 1.14 - 1.15.2", v1_13, v1_13_2),
    v1_15("MC 1.15.x", v1_13, v1_13_2, v1_14), // not used
    v1_16("MC 1.16.x", v1_13, v1_13_2, v1_15, v1_14),
    v1_17("MC 1.17.x", v1_13, v1_13_2, v1_14, v1_15, v1_16),
    UNKNOWN("Unknown");

    @NotNull
    private final String name;
    private final CollectionList<BukkitVersion> bukkitVersions;

    BukkitVersion(@NotNull String name, BukkitVersion... bukkitVersions) {
        this.name = name;
        this.bukkitVersions = (CollectionList<BukkitVersion>) ICollectionList.asList(bukkitVersions).thenAdd(this);
    }

    public CollectionList<BukkitVersion> getBukkitVersions() {
        return bukkitVersions;
    }

    public boolean atLeast(@NotNull BukkitVersion bukkitVersion) {
        return bukkitVersions.contains(bukkitVersion);
    }

    @NotNull
    public String getName() {
        return this.name;
    }
}
