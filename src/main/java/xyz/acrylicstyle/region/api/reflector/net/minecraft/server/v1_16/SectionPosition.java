package xyz.acrylicstyle.region.api.reflector.net.minecraft.server.v1_16;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.reflect.Ref;
import util.reflector.CastTo;
import util.reflector.ForwardMethod;
import util.reflector.Reflector;
import util.reflector.ReflectorHandler;
import util.reflector.Static;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

public interface SectionPosition {
    static @NotNull SectionPosition getInstance(@Nullable Object o) {
        ReflectorHandler handler = new ReflectorHandler(Ref.forName(ReflectionUtil.getNMSPackage() + ".SectionPosition").getClazz(), o);
        return Reflector.newReflector(null, xyz.acrylicstyle.region.api.reflector.net.minecraft.server.v1_16_R2.SectionPosition.class, handler);
    }

    @NotNull
    @CastTo(SectionPosition.class)
    @Static
    @ForwardMethod("a")
    SectionPosition create(int sectionX, int sectionY, int sectionZ);

    long toLong();
}
