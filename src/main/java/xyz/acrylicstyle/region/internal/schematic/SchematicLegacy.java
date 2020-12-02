package xyz.acrylicstyle.region.internal.schematic;

import net.querz.nbt.tag.CompoundTag;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import util.CollectionList;
import util.ICollectionList;
import xyz.acrylicstyle.region.api.block.state.BlockState;
import xyz.acrylicstyle.region.api.exception.RegionEditException;
import xyz.acrylicstyle.region.api.schematic.AbstractSchematic;
import xyz.acrylicstyle.region.internal.block.Blocks;
import xyz.acrylicstyle.tomeito_api.utils.Log;

import java.util.concurrent.atomic.AtomicInteger;

// todo: not tested yet
public final class SchematicLegacy extends AbstractSchematic {
    private ICollectionList<BlockState> cache = null;

    public SchematicLegacy(@NotNull CompoundTag tag) { super(tag); }

    @Override
    public @NotNull ICollectionList<BlockState> getBlocks() {
        if (cache != null) return cache;
        CollectionList<BlockState> blocks = new CollectionList<>();
        AtomicInteger index = new AtomicInteger();
        byte[] dataTag = tag.getByteArrayTag("Data").getValue();
        int maxWidth  = tag.getShort("Width")  - 1; // x
        int maxHeight = tag.getShort("Height") - 1; // y
        int maxLength = tag.getShort("Length") - 1; // z
        AtomicInteger width = new AtomicInteger();
        AtomicInteger height = new AtomicInteger();
        AtomicInteger length = new AtomicInteger();
        byte[] arr = tag.getByteArray("Blocks");
        boolean warnLogged = false;
        for (byte i : arr) {
            warnLogged = SchematicUtil.checkConditions(maxWidth, maxHeight, maxLength, width, height, length, warnLogged);
            byte data = dataTag[index.getAndIncrement()];
            Material material = Blocks.getMaterialById(i);
            if (material == null) throw new RegionEditException("Could not resolve material by (combined) id " + i + " (incompatible version?)");
            blocks.add(new BlockState(material, data, null, new Location(null, width.get(), height.get(), length.get())));
        }
        long ex = maxWidth * maxHeight * maxLength;
        Log.info("---------- Schematic Details (Legacy) ----------");
        Log.info("Max X: " + maxWidth);
        Log.info("Max Y: " + maxHeight);
        Log.info("Max Z: " + maxLength);
        Log.info("Current X: " + width.get());
        Log.info("Current Y: " + height.get());
        Log.info("Current Z: " + length.get());
        Log.info("Palette Max: " + tag.getInt("PaletteMax"));
        Log.info("Expected blocks: " + ex + ", BlockData: " + arr.length);
        Log.info("Actual blocks: " + blocks.size() + " (diff: " + (blocks.size() - ex) + ")");
        Log.info("---------------------------------------------");
        return cache = blocks;
    }
}
