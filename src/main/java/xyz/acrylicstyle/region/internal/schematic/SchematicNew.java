package xyz.acrylicstyle.region.internal.schematic;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import util.Collection;
import util.CollectionList;
import util.ICollectionList;
import util.reflect.Ref;
import xyz.acrylicstyle.region.api.MinecraftKey;
import xyz.acrylicstyle.region.api.block.state.BlockState;
import xyz.acrylicstyle.region.api.block.state.BlockStatePropertyMap;
import xyz.acrylicstyle.region.api.schematic.AbstractSchematic;
import xyz.acrylicstyle.tomeito_api.utils.Log;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public final class SchematicNew extends AbstractSchematic {
    public SchematicNew(@NotNull CompoundTag tag) { super(tag); }

    @Override
    public @NotNull CollectionList<BlockState> getBlocks() {
        Collection<Integer, BlockState> palette = new Collection<>();
        int maxWidth  = tag.getShort("Width")  - 1; // x
        int maxHeight = tag.getShort("Height") - 1; // y
        int maxLength = tag.getShort("Length") - 1; // z
        AtomicInteger width = new AtomicInteger();
        AtomicInteger height = new AtomicInteger();
        AtomicInteger length = new AtomicInteger();
        tag.getCompoundTag("Palette").forEach((b, id) -> {
            Material type = ICollectionList.asList(Material.values()).filter(material -> {
                String[] arr = b.replaceFirst("(.*)\\[.*", "$1").split(":");
                MinecraftKey k = new MinecraftKey(arr[0], arr[1]);
                MinecraftKey key = new MinecraftKey(Ref.getClass(Material.class).getMethod("getKey").invoke(material));
                return k.getKey().equals(key.getKey());
            }).first();
            BlockStatePropertyMap propertyMap = BlockStatePropertyMap.parse(b);
            palette.add(((IntTag) id).asInt(), new BlockState(Objects.requireNonNull(type), (byte) 0, propertyMap));
        });
        CollectionList<BlockState> blocks = new CollectionList<>();

        for (byte i : tag.getByteArray("BlockData")) {
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
            BlockState state = palette.get((int) i);
            blocks.add(new BlockState(state.getType(), state.getData(), state.getPropertyMap(), new Location(null, width.getAndIncrement(), height.get(), length.get())));
        }
        return blocks;
    }
}
