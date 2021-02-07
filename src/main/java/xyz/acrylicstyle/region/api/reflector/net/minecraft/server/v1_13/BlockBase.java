package xyz.acrylicstyle.region.api.reflector.net.minecraft.server.v1_13;

import org.jetbrains.annotations.Nullable;
import util.reflector.CastTo;
import util.reflector.ReflectorOption;
import xyz.acrylicstyle.region.api.reflector.org.bukkit.craftbukkit.v1_13.block.data.CraftBlockData;

public interface BlockBase {
    interface BlockData {
        @ReflectorOption(errorOption = ReflectorOption.ErrorOption.RETURN_NULL)
        @Nullable
        @CastTo(CraftBlockData.class)
        CraftBlockData createCraftBlockData();
    }
}
