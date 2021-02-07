package xyz.acrylicstyle.region.api.reflector.net.minecraft.server.v1_16_R2;

import org.jetbrains.annotations.NotNull;
import util.reflector.CastTo;
import util.reflector.ForwardMethod;
import util.reflector.Static;

public interface SectionPosition extends xyz.acrylicstyle.region.api.reflector.net.minecraft.server.v1_16.SectionPosition {
    @NotNull
    @CastTo(SectionPosition.class)
    @ForwardMethod("a")
    @Static
    @Override
    SectionPosition create(int sectionX, int sectionY, int sectionZ);

    @ForwardMethod("s")
    @Override
    long toLong();
}
