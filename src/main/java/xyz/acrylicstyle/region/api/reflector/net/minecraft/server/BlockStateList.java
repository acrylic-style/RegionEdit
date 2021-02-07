package xyz.acrylicstyle.region.api.reflector.net.minecraft.server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.reflector.CastTo;
import util.reflector.ForwardMethod;
import util.reflector.Reflector;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

public interface BlockStateList {
    @Nullable
    static BlockStateList getInstance(@NotNull Object o) {
        return Reflector.newReflector(null, BlockStateList.class, ReflectionUtil.getNMSPackage() + ".BlockStateList", o);
    }

    @NotNull
    @ForwardMethod("getBlockData")
    @CastTo(IBlockData.class)
    IBlockData getBlockData();
}
