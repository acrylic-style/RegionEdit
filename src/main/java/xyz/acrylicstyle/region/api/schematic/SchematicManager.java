package xyz.acrylicstyle.region.api.schematic;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.region.api.RegionEdit;

import java.io.File;
import java.io.IOException;

public final class SchematicManager {
    // requires loaded plugin to work
    @Nullable
    public static Schematic load(@NotNull File file) {
        try {
            if (!file.exists()) return null;
            CompoundTag tag = (CompoundTag) NBTUtil.read(file).getTag();
            String materials = tag.getString("Materials");
            SchematicFormat format = SchematicFormat.MODERN;
            if (materials != null && !materials.equals("")) {
                if (!materials.equals("Alpha"))
                    throw new IllegalArgumentException("Unsupported Materials tag: " + materials);
                format = SchematicFormat.LEGACY;
            }
            return RegionEdit.getInstance().createSchematic(format, tag);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void save(@NotNull Schematic schematic, @NotNull File file) {
        try {
            NBTUtil.write(schematic.getTag(), file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
