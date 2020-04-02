package xyz.acrylicstyle.region.internal.nms;

import xyz.acrylicstyle.tomeito_core.utils.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;

public class ChunkSection {
    private Object o;

    public ChunkSection(Object o) {
        this.o = o;
    }

    public void setType(int x, int y, int z, Object blockData) {
        try {
            ReflectionUtil.getNMSClass("ChunkSection")
                    .getMethod("setType", int.class, int.class, int.class, ReflectionUtil.getNMSClass("IBlockData"))
                    .invoke(o, x, y, z, blockData);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
