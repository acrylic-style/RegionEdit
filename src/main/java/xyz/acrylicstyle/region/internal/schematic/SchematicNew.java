package xyz.acrylicstyle.region.internal.schematic;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import util.Collection;
import util.ICollectionList;
import util.file.FileBasedCollectionList;
import util.reflect.Ref;
import xyz.acrylicstyle.region.api.MinecraftKey;
import xyz.acrylicstyle.region.api.block.state.BlockState;
import xyz.acrylicstyle.region.api.block.state.BlockStatePropertyMap;
import xyz.acrylicstyle.region.api.schematic.AbstractSchematic;
import xyz.acrylicstyle.region.api.util.ByteToInt;
import xyz.acrylicstyle.tomeito_api.utils.Log;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

// todo: make sure this doesn't do anything stupid
public final class SchematicNew extends AbstractSchematic {
    public SchematicNew(@NotNull CompoundTag tag) { super(tag); }

    @Override
    public @NotNull ICollectionList<BlockState> getBlocks() {
        Collection<Integer, BlockState> palette = new Collection<>();
        int maxWidth  = tag.getShort("Width")  - 1; // x
        int maxHeight = tag.getShort("Height") - 1; // y
        int maxLength = tag.getShort("Length") - 1; // z
        @NotNull final AtomicInteger width = new AtomicInteger();
        @NotNull final AtomicInteger height = new AtomicInteger();
        @NotNull final AtomicInteger length = new AtomicInteger();
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
        ICollectionList<BlockState> blocks = new FileBasedCollectionList<>();
        boolean warnLogged = false;
        byte[] arr = tag.getByteArray("BlockData");
        for (byte i : arr) {
            if (width.get() > maxWidth) {
                width.set(0);
                length.incrementAndGet();
            }
            if (length.get() > maxLength) {
                length.set(0);
                height.incrementAndGet();
            }
            if (height.get() > maxHeight) {
                if (!warnLogged) {
                    Log.warn("Current height is higher than maximum value! (curr: " + height.get() + ", max: " + maxHeight + ")");
                    warnLogged = true;
                }
                //height.set(0);
            }
            BlockState state = palette.get(ByteToInt.b2i(i));
            if (state == null) {
                Log.error("Missing palette for: " + i);
                continue;
            }
            blocks.add(new BlockState(state.getType(), state.getData(), state.getPropertyMap(), new Location(null, width.getAndIncrement(), height.get(), length.get())));
        }
        long ex = maxWidth * maxHeight * maxLength;
        Log.info("---------- Schematic Details (New) ----------");
        Log.info("Max X: " + maxWidth);
        Log.info("Max Y: " + maxHeight);
        Log.info("Max Z: " + maxLength);
        Log.info("Current X: " + width.get());
        Log.info("Current Y: " + height.get());
        Log.info("Current Z: " + length.get());
        Log.info("Palette Max: " + tag.getInt("PaletteMax"));
        Log.info("Palette Size: " + palette.size());
        Log.info("Expected blocks: " + ex + ", BlockData: " + arr.length);
        Log.info("Actual blocks: " + blocks.size() + " (diff: " + (blocks.size() - ex) + ")");
        Log.info("------------------------------------------");
        palette.clear();
        return blocks;
    }
}
