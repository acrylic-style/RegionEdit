package xyz.acrylicstyle.region.internal.nms;

import util.ICollectionList;
import util.ReflectionHelper;
import xyz.acrylicstyle.region.internal.utils.Compatibility;
import xyz.acrylicstyle.shared.NMSAPI;
import xyz.acrylicstyle.tomeito_core.utils.ReflectionUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
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
        try {
            Field field = Objects.requireNonNull(ReflectionHelper.findField(ReflectionUtil.getNMSClass("Chunk"), "sections"));
            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.setAccessible(true);
            Object arr = Array.newInstance(ReflectionUtil.getNMSClass("ChunkSection"), this.sections.length);
            ICollectionList.asList(this.sections).map(ChunkSection::getNMSClass).foreach((o, i) -> Array.set(arr, i, o));
            field.set(getNMSClass(), arr);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public void initLighting() {
        /*
        if (Compatibility.checkLightEngine()) {
            try {
                Object SKY = ReflectionUtil.getNMSClass("EnumSkyBlock").getField("SKY").get(null);
                Object BLOCK = ReflectionUtil.getNMSClass("EnumSkyBlock").getField("BLOCK").get(null);
                Object sky = invoke("e")
                        .getClass()
                        .getMethod("a", ReflectionUtil.getNMSClass("EnumSkyBlock"))
                        .invoke(o, SKY);
                Object block = invoke("e")
                        .getClass()
                        .getMethod("a", ReflectionUtil.getNMSClass("EnumSkyBlock"))
                        .invoke(o, BLOCK);
                sky.getClass().getMethod("a", ReflectionUtil.getNMSClass("BlockPosition")).invoke(sky, new BlockPosition())
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        } else {
            invoke("initLighting");
        }
         */
        if (!Compatibility.checkLightEngine()) invoke("initLighting");
    }

    public void setType(Object blockPosition, Object blockData, boolean applyPhysics) {
        try {
            Objects.requireNonNull(ReflectionHelper.findMethod(
                    ReflectionUtil.getNMSClass("Chunk"),
                    "setType",
                    ReflectionUtil.getNMSClass("BlockPosition"),
                    ReflectionUtil.getNMSClass("IBlockData"),
                    boolean.class
            )).invoke(o, blockPosition, blockData, applyPhysics);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
}
