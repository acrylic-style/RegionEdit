package xyz.acrylicstyle.region.internal.nms;

import sun.misc.Unsafe;
import util.ICollectionList;
import util.ReflectionHelper;
import xyz.acrylicstyle.minecraft.BlockPosition;
import xyz.acrylicstyle.shared.NMSAPI;
import xyz.acrylicstyle.tomeito_core.utils.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * Partial implementation of NMS Chunk.
 */
public class Chunk extends NMSAPI {
    public Chunk(Object o) {
        super(o, "Chunk");
        this.sections = ICollectionList
                .asList((Object[]) getField("sections"))
                .map((s, i) -> s == null ? new ChunkSection(this, i << 4) : new ChunkSection(this, s))
                .toArray(new ChunkSection[0]);
    }

    public final ChunkSection[] sections;

    public static Chunk wrap(org.bukkit.Chunk chunk) {
        try {
            return new Chunk(ReflectionHelper.invokeMethod(chunk.getClass(), chunk, "getHandle"));
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    // uses Unsafe method for faster operation
    public void save() {
        Unsafe unsafe = (Unsafe) Objects.requireNonNull(ReflectionHelper.getFieldWithoutException(Unsafe.class, null, "theUnsafe"));
        try {
            Field field = Objects.requireNonNull(ReflectionHelper.findField(ReflectionUtil.getNMSClass("Chunk"), "sections"));
            unsafe.putObject(getNMSClass(), unsafe.objectFieldOffset(field), ICollectionList.asList(this.sections).map(ChunkSection::getNMSClass).toArray(new Object[0]));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void initLighting() {
        invoke("initLighting");
    }

    public void setType(BlockPosition blockPosition, Object blockData, boolean applyPhysics) {
        invoke("setType", blockPosition.toNMSClass(), blockData, applyPhysics);
    }
}
