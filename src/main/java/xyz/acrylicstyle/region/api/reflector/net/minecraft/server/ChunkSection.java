package xyz.acrylicstyle.region.api.reflector.net.minecraft.server;

import org.jetbrains.annotations.NotNull;
import util.reflect.Ref;
import util.reflector.Reflector;
import util.reflector.ReflectorHandler;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

public interface ChunkSection {
    @NotNull
    static ChunkSection getInstance(@NotNull Object o) {
        return Reflector.newReflector(null, ChunkSection.class, new ReflectorHandler(Ref.forName(ReflectionUtil.getNMSPackage() + ".ChunkSection").getClazz(), o));
    }
}
