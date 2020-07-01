package xyz.acrylicstyle.region.internal.nms;

import xyz.acrylicstyle.minecraft.Packet;
import xyz.acrylicstyle.minecraft.PacketDataSerializer;
import xyz.acrylicstyle.minecraft.PacketListener;
import xyz.acrylicstyle.minecraft.PacketListenerPlayOut;
import xyz.acrylicstyle.region.internal.utils.Compatibility;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;

public class PacketPlayOutMapChunk implements Packet<PacketListenerPlayOut> {
    private final Object o;

    public PacketPlayOutMapChunk(Chunk chunk) {
        try {
            if (Compatibility.checkPacketPlayOutMapChunk1_8Constructor()) {
                this.o = ReflectionUtil.getNMSClass("PacketPlayOutMapChunk")
                        .getConstructor(ReflectionUtil.getNMSClass("Chunk"), boolean.class, int.class)
                        .newInstance(chunk.getNMSClass(), true, 20);
            } else if (Compatibility.checkPacketPlayOutMapChunk1_16Constructor()) {
                this.o = ReflectionUtil.getNMSClass("PacketPlayOutMapChunk")
                        .getConstructor(ReflectionUtil.getNMSClass("Chunk"), int.class, boolean.class)
                        .newInstance(chunk.getNMSClass(), 20, true);
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
    public void a(PacketListenerPlayOut packetListener) {
        try {
            ReflectionUtil.getNMSClass("PacketPlayOutMapChunk")
                    .getMethod("a", ReflectionUtil.getNMSClass("PacketListenerPlayOut"))
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
