package xyz.acrylicstyle.region.api.reflector.net.minecraft.server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.reflect.Ref;
import util.reflector.Reflector;
import util.reflector.ReflectorHandler;
import xyz.acrylicstyle.region.api.reflector.net.minecraft.server.v1_13.BlockBase;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

public interface IBlockData extends BlockBase.BlockData {
    static @NotNull IBlockData getInstance(@Nullable Object o) {
        return Reflector.newReflector(null, IBlockData.class, new ReflectorHandler(Ref.forName(ReflectionUtil.getNMSPackage() + ".IBlockData").getClazz(), o));
    }

    Object getBlock();

    <T extends Comparable<T>, V extends T> IBlockData set(IBlockState<T> blockState, V value);
}
