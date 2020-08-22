package xyz.acrylicstyle.region.api.schematic;

import net.querz.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public abstract class AbstractSchematic implements Schematic {
    protected final CompoundTag tag;

    public AbstractSchematic(@NotNull CompoundTag tag) { this.tag = tag; }

    @Override
    @NotNull
    public final CompoundTag getTag() { return tag; }

    @Override
    public final void save(@NotNull File file) { SchematicManager.save(this, file); }
}
