package xyz.acrylicstyle.region.internal.schematic;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import util.CollectionList;
import xyz.acrylicstyle.region.api.block.state.BlockState;
import xyz.acrylicstyle.region.api.schematic.AbstractSchematic;
import xyz.acrylicstyle.region.internal.block.Blocks;
import xyz.acrylicstyle.tomeito_api.utils.Log;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public final class SchematicLegacy extends AbstractSchematic {
    public SchematicLegacy(@NotNull CompoundTag tag) { super(tag); }

    @Override
    public @NotNull CollectionList<BlockState> getBlocks() {
        CollectionList<BlockState> blocks = new CollectionList<>();
        AtomicInteger index = new AtomicInteger();
        ListTag<IntTag> dataTag = tag.getListTag("Data").asIntTagList();
        int maxWidth  = tag.getShort("Width")  - 1; // x
        int maxHeight = tag.getShort("Height") - 1; // y
        int maxLength = tag.getShort("Length") - 1; // z
        AtomicInteger width = new AtomicInteger();
        AtomicInteger height = new AtomicInteger();
        AtomicInteger length = new AtomicInteger();
        for (byte i : tag.getByteArray("Blocks")) {
            if (width.get() > maxWidth) {
                width.set(0);
                length.incrementAndGet();
            }
            if (length.get() > maxLength) {
                length.set(0);
                height.incrementAndGet();
            }
            if (height.get() > maxHeight) {
                Log.warn("Resetting height to 0 (curr: " + height.get() + ", max: " + maxHeight + ")");
                height.set(0);
            }
            byte data = dataTag.get(index.getAndIncrement()).asByte();
            blocks.add(new BlockState(Objects.requireNonNull(Blocks.getMaterialById(i)), data, null, new Location(null, width.get(), height.get(), length.get())));
        }
        return blocks;
    }
}
