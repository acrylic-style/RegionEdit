package xyz.acrylicstyle.region.internal.nms;

import util.ICollectionList;
import util.ReflectionHelper;
import xyz.acrylicstyle.minecraft.BlockPosition;
import xyz.acrylicstyle.shared.NMSAPI;

import java.lang.reflect.InvocationTargetException;

public class Chunk extends NMSAPI {
    public Chunk(Object o) {
        super(o, "Chunk");
        this.sections = ICollectionList.asList((Object[]) getField("sections")).map(ChunkSection::new).toArray(new ChunkSection[0]);
    }

    public final ChunkSection[] sections;

    public static Chunk wrap(org.bukkit.Chunk chunk) {
        try {
            return new Chunk(ReflectionHelper.invokeMethod(chunk.getClass(), chunk, "getHandle"));
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void setType(BlockPosition blockPosition, Object blockData, boolean applyPhysics) {
        invoke("setType", blockPosition.toNMSClass(), blockData, applyPhysics);
    }
}
