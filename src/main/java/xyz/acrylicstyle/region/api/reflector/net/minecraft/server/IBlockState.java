package xyz.acrylicstyle.region.api.reflector.net.minecraft.server;

import org.jetbrains.annotations.NotNull;
import util.reflect.Ref;
import util.reflector.Reflector;
import util.reflector.ReflectorHandler;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

public interface IBlockState<T extends Comparable<T>> {
    @SuppressWarnings("unchecked")
    static <T extends Comparable<T>> @NotNull IBlockState<T> make(Object o) {
        return Reflector.newReflector(null, IBlockState.class, new ReflectorHandler(Ref.forName(ReflectionUtil.getNMSPackage() + ".IBlockState").getClazz(), o));
    }
}
