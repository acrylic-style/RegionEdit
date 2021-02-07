package xyz.acrylicstyle.region.api.reflector.net.minecraft.server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.reflect.Ref;
import util.reflector.CastTo;
import util.reflector.ConstructorCall;
import util.reflector.Reflector;
import util.reflector.ReflectorHandler;
import util.reflector.TransformParam;
import util.reflector.Type;
import xyz.acrylicstyle.region.api.reflector.net.minecraft.server.v1_16.SectionPosition;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

import java.util.Set;

public interface PacketPlayOutMultiBlockChange {
    @NotNull
    static PacketPlayOutMultiBlockChange getInstance(@Nullable Object o) {
        return Reflector.newReflector(null, PacketPlayOutMultiBlockChange.class, new ReflectorHandler(Ref.forName(ReflectionUtil.getNMSPackage() + ".PacketPlayOutMultiBlockChange").getClazz(), o));
    }

    @ConstructorCall
    @NotNull
    @CastTo(PacketPlayOutMultiBlockChange.class)
    PacketPlayOutMultiBlockChange constructor_v18_v1161(int length, short[] locations, @TransformParam Chunk chunk);

    @ConstructorCall
    @NotNull
    @CastTo(PacketPlayOutMultiBlockChange.class)
    PacketPlayOutMultiBlockChange constructor_v1162(@TransformParam SectionPosition sectionPosition, @Type("org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.shorts.ShortSet") Set<Short> shortSet, @TransformParam ChunkSection chunkSection, boolean trustEdges);

    default Object getHandle() {
        return Reflector.getUnproxiedInstance(this).get();
    }
}
