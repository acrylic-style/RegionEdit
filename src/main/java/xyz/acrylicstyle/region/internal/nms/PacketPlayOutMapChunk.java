package xyz.acrylicstyle.region.internal.nms;

import xyz.acrylicstyle.minecraft.Packet;
import xyz.acrylicstyle.minecraft.PacketDataSerializer;
import xyz.acrylicstyle.minecraft.PacketListener;
import xyz.acrylicstyle.region.internal.utils.Compatibility;
import xyz.acrylicstyle.tomeito_core.utils.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;

public class PacketPlayOutMapChunk implements Packet {
    private Object o;

    public PacketPlayOutMapChunk(Chunk chunk) {
        try {
            if (Compatibility.checkPacketPlayOutMapChunkOldConstructor()) {
                this.o = ReflectionUtil.getNMSClass("PacketPlayOutMapChunk")
                        .getConstructor(ReflectionUtil.getNMSClass("Chunk"), boolean.class, int.class)
                        .newInstance(chunk.getNMSClass(), true, 20);
            } else {
                this.o = ReflectionUtil.getNMSClass("PacketPlayOutMapChunk")
                        .getConstructor(ReflectionUtil.getNMSClass("Chunk"), int.class)
                        .newInstance(chunk.getNMSClass(), 20);
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getNMSClass() { return o; }

    @Override
    public void a(PacketDataSerializer packetDataSerializer) {
        try {
            ReflectionUtil.getNMSClass("PacketPlayOutMapChunk")
                    .getMethod("a", ReflectionUtil.getNMSClass("PacketDataSerializer"))
                    .invoke(getNMSClass(), packetDataSerializer.getHandle());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void b(PacketDataSerializer packetDataSerializer) {
        try {
            ReflectionUtil.getNMSClass("PacketPlayOutMapChunk")
                    .getMethod("b", ReflectionUtil.getNMSClass("PacketDataSerializer"))
                    .invoke(getNMSClass(), packetDataSerializer.getHandle());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void a(PacketListener packetListener) {
        try {
            ReflectionUtil.getNMSClass("PacketPlayOutMapChunk")
                    .getMethod("a", ReflectionUtil.getNMSClass("PacketListener"))
                    .invoke(getNMSClass(), packetListener.getNMSPacketListener());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object toNMSPacket() {
        return o;
    }
}
