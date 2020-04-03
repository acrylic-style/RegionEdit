package xyz.acrylicstyle.region.internal.nms;

import xyz.acrylicstyle.region.internal.utils.Compatibility;
import xyz.acrylicstyle.tomeito_core.utils.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * Partial implementation of NMS ChunkSection.
 */
public class ChunkSection {
    private Object o;
    private final Chunk chunk;

    public ChunkSection(Chunk chunk, Object o) {
        this.chunk = chunk;
        this.o = Objects.requireNonNull(o);
    }

    public Chunk getChunk() {
        return chunk;
    }

    public ChunkSection(Chunk chunk, int i) {
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

    public void setType(int x, int y, int z, Object blockData) {
        try {
            ReflectionUtil.getNMSClass("ChunkSection")
                    .getMethod("setType", int.class, int.class, int.class, ReflectionUtil.getNMSClass("IBlockData"))
                    .invoke(o, x, y, z, blockData);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        chunk.save();
    }

    public Object getNMSClass() {
        return o;
    }
}
