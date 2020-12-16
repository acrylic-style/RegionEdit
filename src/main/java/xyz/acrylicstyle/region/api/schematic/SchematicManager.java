package xyz.acrylicstyle.region.api.schematic;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.CollectionSet;
import util.ICollectionList;
import xyz.acrylicstyle.region.api.RegionEdit;

import java.io.File;
import java.io.IOException;

public final class SchematicManager {
    @Nullable
    public static File findSchematic(@NotNull String name) {
        File file = new File("./plugins/RegionEdit/schematics/" + name + ".schem");
        if (file.exists()) return file;
        file = new File("./plugins/RegionEdit/schematics/" + name + ".schematic");
        if (file.exists()) return file;
        file = new File("./plugins/WorldEdit/schematics/" + name + ".schem");
        if (file.exists()) return file;
        file = new File("./plugins/WorldEdit/schematics/" + name + ".schematic");
        if (file.exists()) return file;
        return null;
    }

    @Contract(pure = true)
    @NotNull
    public static CollectionSet<String> findSchematicFiles() {
        CollectionSet<String> set = new CollectionSet<>();
        File file = new File("./plugins/RegionEdit/schematics/");
        if (file.exists()) {
            set.addAll(ICollectionList.asList(file.listFiles(f -> f.isFile() && f.getName().endsWith(".schem") || f.getName().endsWith(".schematic"))).map(File::getName));
        }
        file = new File("./plugins/WorldEdit/schematics/");
        if (file.exists()) {
            set.addAll(ICollectionList.asList(file.listFiles(f -> f.isFile() && f.getName().endsWith(".schem") || f.getName().endsWith(".schematic"))).map(File::getName));
        }
        return set;
    }

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
