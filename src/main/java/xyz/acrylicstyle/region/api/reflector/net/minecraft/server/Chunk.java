package xyz.acrylicstyle.region.api.reflector.net.minecraft.server;

import org.jetbrains.annotations.NotNull;
import util.reflect.Ref;
import util.reflector.FieldGetter;
import util.reflector.Reflector;
import util.reflector.ReflectorHandler;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

public interface Chunk {
    @NotNull
    static Chunk getInstance(@NotNull Object o) {
        return Reflector.newReflector(null, Chunk.class, new ReflectorHandler(Ref.forName(ReflectionUtil.getNMSPackage() + ".Chunk").getClazz(), o));
    }

    @FieldGetter("locX")
    int getX();

    @FieldGetter("locZ")
    int getZ();
}
