package xyz.acrylicstyle.region.internal.nms;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import util.ActionableResult;
import util.Collection;
import util.ICollectionList;
import util.ReflectionHelper;
import xyz.acrylicstyle.region.api.util.Tuple;
import xyz.acrylicstyle.region.internal.utils.Compatibility;
import xyz.acrylicstyle.shared.NMSAPI;
import xyz.acrylicstyle.tomeito_api.utils.Log;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Partial implementation of NMS Chunk.
 */
public class Chunk extends NMSAPI {
    private static final Collection<Tuple<UUID, Integer, Integer>, Chunk> chunks = new Collection<>();
    private static final Log.Logger LOGGER = Log.with("RegionEdit");
    private static final Object LOCK = new Object();
    public static final Class<?> CLASS = getClassWithoutException("Chunk");
    public final int x;
    public final int z;

    private Chunk(Object o) {
        super(o, "Chunk");
        if (Compatibility.checkChunkSection()) {
            this.sections = ICollectionList
                    .asList((Object[]) getField("sections"))
                    .map((section, i) -> section == null ? new ChunkSection(this, i << 4) : new ChunkSection(this, section))
                    .toArray(new ChunkSection[0]);
        } else {
            this.sections = new ChunkSection[0];
        }
        Object loc = ActionableResult.ofThrowable(() -> ReflectionHelper.getField(CLASS, this.o, "loc")).nullableValue();
        this.x = (int) ActionableResult.ofThrowable(() -> ReflectionHelper.getField(CLASS, this.o, "locX")).orElseGet(() -> ReflectionHelper.getFieldWithoutException(getClassWithoutException("ChunkCoordIntPair"), loc, "x"));
        this.z = (int) ActionableResult.ofThrowable(() -> ReflectionHelper.getField(CLASS, this.o, "locZ")).orElseGet(() -> ReflectionHelper.getFieldWithoutException(getClassWithoutException("ChunkCoordIntPair"), loc, "z"));
    }

    public final ChunkSection[] sections;

    public static @NotNull Chunk getInstance(org.bukkit.Chunk chunk) {
        Chunk result = chunks.get(new Tuple<>(chunk.getWorld().getUID(), chunk.getX(), chunk.getZ()));
        if (result == null) {
            result = new Chunk(ReflectionHelper.invokeMethodWithoutException(chunk.getClass(), chunk, "getHandle"));
            chunks.add(new Tuple<>(chunk.getWorld().getUID(), chunk.getX(), chunk.getZ()), result);
        }
        return result;
    }

    /*
    public static Chunk getInstance(Object o) {
        return new Chunk(o);
    }
    */

    public void save() {
        synchronized (LOCK) {
            try {
                Field field = Objects.requireNonNull(ReflectionHelper.findField(ReflectionUtil.getNMSClass("Chunk"), "sections"));
                field.setAccessible(true);
                Object arr = field.get(getNMSClass());
                ICollectionList.asList(this.sections).map(ChunkSection::getNMSClass).foreach((o, i) -> {
                    if (Array.get(arr, i) == null) {
                        Array.set(arr, i, o);
                    }
                });
            } catch (ReflectiveOperationException e) {
                LOGGER.warn("Failed to save ChunkSection at Chunk[" + x + ", " + z + "]");
                e.printStackTrace();
            }
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

    @Contract(pure = true)
    @NotNull
    public static Map.Entry<Integer, Integer> getChunkPos(Object o) {
        Object loc = ActionableResult.ofThrowable(() -> ReflectionHelper.getField(CLASS, o, "loc")).nullableValue();
        int x = (int) ActionableResult.ofThrowable(() -> ReflectionHelper.getField(CLASS, o, "locX")).orElseGet(() -> ReflectionHelper.getFieldWithoutException(getClassWithoutException("ChunkCoordIntPair"), loc, "x"));
        int z = (int) ActionableResult.ofThrowable(() -> ReflectionHelper.getField(CLASS, o, "locZ")).orElseGet(() -> ReflectionHelper.getFieldWithoutException(getClassWithoutException("ChunkCoordIntPair"), loc, "z"));
        return new AbstractMap.SimpleImmutableEntry<>(x, z);
    }
}
