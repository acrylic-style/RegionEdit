package xyz.acrylicstyle.region.api.reflector.org.bukkit.craftbukkit.util;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;
import util.reflect.Ref;
import util.reflector.CastTo;
import util.reflector.Reflector;
import util.reflector.ReflectorHandler;
import xyz.acrylicstyle.region.api.reflector.net.minecraft.server.IBlockData;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

public interface CraftMagicNumbers {
    CraftMagicNumbers INSTANCE = getInstance();

    static CraftMagicNumbers getInstance() {
        return Reflector.newReflector(null, CraftMagicNumbers.class, new ReflectorHandler(Ref.forName(ReflectionUtil.getCraftBukkitPackage() + ".util.CraftMagicNumbers").getClazz(), null));
    }

    @CastTo(IBlockData.class)
    IBlockData getBlock(@NotNull MaterialData materialData);

    @CastTo(IBlockData.class)
    IBlockData getBlock(@NotNull Material material, byte data);
}
