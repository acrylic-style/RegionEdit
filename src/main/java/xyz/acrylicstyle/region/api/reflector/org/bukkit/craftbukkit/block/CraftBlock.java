package xyz.acrylicstyle.region.api.reflector.org.bukkit.craftbukkit.block;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.reflect.Ref;
import util.reflector.CastTo;
import util.reflector.Reflector;
import util.reflector.ReflectorHandler;
import util.reflector.ReflectorOption;
import util.reflector.TransformParam;
import xyz.acrylicstyle.region.api.reflector.net.minecraft.server.Block;
import xyz.acrylicstyle.region.api.reflector.org.bukkit.craftbukkit.v1_13.block.data.CraftBlockData;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

public interface CraftBlock {
    static @NotNull CraftBlock getInstance(@NotNull org.bukkit.block.Block block) {
        return Reflector.newReflector(null, CraftBlock.class, new ReflectorHandler(Ref.forName(ReflectionUtil.getCraftBukkitPackage() + ".block.CraftBlock").getClazz(), block));
    }

    /**
     * Get NMS IBlockData for this block.
     * @return the block
     */
    @NotNull
    @CastTo(Block.class)
    Block getNMSBlock();

    @ReflectorOption(errorOption = ReflectorOption.ErrorOption.RETURN_NULL)
    @Nullable
    @CastTo(CraftBlockData.class)
    CraftBlockData getBlockData();

    @ReflectorOption(errorOption = ReflectorOption.ErrorOption.RETURN_NULL)
    void setBlockData(@TransformParam CraftBlockData blockData);
}
