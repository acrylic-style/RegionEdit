package xyz.acrylicstyle.region.internal.nms;

import util.ActionableResult;
import util.reflect.RefClass;
import util.reflect.RefMethod;
import xyz.acrylicstyle.region.internal.utils.BukkitVersion;
import xyz.acrylicstyle.region.internal.utils.Compatibility;
import xyz.acrylicstyle.shared.NMSAPI;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

import java.util.Objects;

/**
 * Partial implementation of NMS ChunkSection.
 */
public class ChunkSection {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final RefClass<Object> CLASS = new RefClass(NMSAPI.getClassWithoutException("ChunkSection"));
    public static final RefMethod<Object> setType = ActionableResult.ofThrowable(() ->
            CLASS.getMethod("setType", int.class, int.class, int.class, ReflectionUtil.getNMSClass("IBlockData"))).get();

    private final Object o;
    private final Chunk chunk;

    protected ChunkSection(Chunk chunk, Object o) {
        this.chunk = chunk;
        this.o = Objects.requireNonNull(o);
    }

    protected ChunkSection(Chunk chunk, int i) {
        this.chunk = chunk;
        try {
            if (Compatibility.checkOldChunkSectionConstructor()) {
                this.o = ReflectionUtil.getNMSClass("ChunkSection").getConstructor(int.class, boolean.class).newInstance(i, true);
            } else {
                this.o = ReflectionUtil.getNMSClass("ChunkSection").getConstructor(int.class).newInstance(i);
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public Chunk getChunk() { return chunk; }

    private static final Object LOCK = new Object();
    public void setType(int x, int y, int z, Object blockData) {
        // [1.14+] try to invoke ChunkSection#setType(int, int, int, IBlockData, boolean)
        ActionableResult.ofThrowable(() -> {
            return CLASS.getMethod("setType", int.class, int.class, int.class, ReflectionUtil.getNMSClass("IBlockData"), boolean.class)
                    .invoke(o, x & 15, y & 15, z & 15, blockData, false); // false = disables reentrant lock, allowing us to write block data on multiple threads
        }).orElseGet(() -> {
            // [all versions] if that doesn't work, try ChunkSection#setType(int, int, int, IBlockData)
            // disabling reentrant lock was introduced at 1.14, but reentrant lock is also exist at 1.13 but we cannot disable it, so we synchronize the block instead.
            if (Compatibility.BUKKIT_VERSION == BukkitVersion.v1_13 || Compatibility.BUKKIT_VERSION == BukkitVersion.v1_13_2) {
                synchronized (LOCK) {
                    return setType.invoke(o, x & 15, y & 15, z & 15, blockData);
                }
            }
            // otherwise we can modify blocks without synchronizing because they didn't have reentrant lock at that point
            return setType.invoke(o, x & 15, y & 15, z & 15, blockData);
        });
        chunk.save();
    }

    public Object getNMSClass() { return o; }
}
