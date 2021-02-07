package xyz.acrylicstyle.region.api.reflector.org.bukkit.craftbukkit.v1_13.block.data;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import util.reflect.Ref;
import util.reflector.CastTo;
import util.reflector.Reflector;
import util.reflector.ReflectorHandler;
import util.reflector.TransformParam;
import xyz.acrylicstyle.region.api.reflector.net.minecraft.server.IBlockData;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

public interface CraftBlockData {
    @NotNull
    static CraftBlockData getInstance(@NotNull Object o) {
        return Reflector.newReflector(null, CraftBlockData.class, new ReflectorHandler(Ref.forName(ReflectionUtil.getCraftBukkitPackage() + ".block.data.CraftBlockData").getClazz(), o));
    }

    @NotNull
    @CastTo(CraftBlockData.class)
    CraftBlockData merge(@TransformParam CraftBlockData blockData);

    boolean matches(@TransformParam CraftBlockData blockData);

    @NotNull
    @CastTo(IBlockData.class)
    IBlockData getState();

    @NotNull
    Material getMaterial();

    @NotNull
    String getAsString(boolean flag);

    @NotNull
    String getAsString();
}
