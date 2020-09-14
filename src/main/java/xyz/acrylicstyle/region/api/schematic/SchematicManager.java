package xyz.acrylicstyle.region.api.schematic;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.region.api.RegionEdit;

import java.io.File;
import java.io.IOException;

public final class SchematicManager {
    /**
     * Loads schematic from file, and returns the loaded schematic.
     * This method requires plugin to be loaded.
     * @param file the schematic file, if not found, the null will be returned.
     * @return schematic if successfully loaded, null if file wasn't found or the an error occurred while loading schematic
     */
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
            return RegionEdit.getInstance().loadSchematic(format, tag);
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            return null;
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
