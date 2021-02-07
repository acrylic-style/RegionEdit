package xyz.acrylicstyle.region.api.reflector.org.bukkit.craftbukkit.v1_13;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.reflector.CastTo;
import util.reflector.Reflector;
import util.reflector.ReflectorHandler;
import util.reflector.ReflectorOption;
import xyz.acrylicstyle.region.api.reflector.org.bukkit.craftbukkit.v1_13.block.data.CraftBlockData;

public interface Material {
    static Material getInstance(@NotNull org.bukkit.Material material) {
        return Reflector.newReflector(null, Material.class, new ReflectorHandler(Material.class, material));
    }

    @ReflectorOption(errorOption = ReflectorOption.ErrorOption.RETURN_NULL)
    @Nullable
    @CastTo(CraftBlockData.class)
    CraftBlockData createBlockData();
}
