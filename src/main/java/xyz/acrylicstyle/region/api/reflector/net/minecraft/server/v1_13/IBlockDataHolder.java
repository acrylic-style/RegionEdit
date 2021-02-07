package xyz.acrylicstyle.region.api.reflector.net.minecraft.server.v1_13;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.reflector.FieldGetter;
import util.reflector.Reflector;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

public interface IBlockDataHolder {
    @Nullable
    static IBlockDataHolder getInstance(@NotNull Object iBlockData) {
        return Reflector.newReflector(null, IBlockDataHolder.class, ReflectionUtil.getNMSPackage() + ".IBlockDataHolder", iBlockData);
    }

    @FieldGetter("c")
    Object getBlock();
}
