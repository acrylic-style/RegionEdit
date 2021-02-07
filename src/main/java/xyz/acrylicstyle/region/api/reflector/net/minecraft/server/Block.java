package xyz.acrylicstyle.region.api.reflector.net.minecraft.server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.reflect.Ref;
import util.reflector.CastTo;
import util.reflector.FieldGetter;
import util.reflector.Reflector;
import util.reflector.ReflectorHandler;
import util.reflector.ReflectorOption;
import util.reflector.Static;
import util.reflector.TransformParam;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

public interface Block {
    Block STATIC = getInstance(null);

    @NotNull
    static Block getInstance(@Nullable Object o) {
        return Reflector.newReflector(null, Block.class, new ReflectorHandler(Ref.forName(ReflectionUtil.getNMSPackage() + ".Block").getClazz(), o));
    }

    @Static
    @CastTo(IBlockData.class)
    @Nullable
    IBlockData getByCombinedId(int id);

    @Static
    int getCombinedId(@TransformParam @NotNull IBlockData iBlockData);

    /**
     * Get blockStateList for this block. Does not exist on 1.8.x.
     * @return blockStateList
     */
    @FieldGetter("blockStateList")
    @ReflectorOption(errorOption = ReflectorOption.ErrorOption.RETURN_NULL)
    @Nullable
    BlockStateList getBlockStateList();

    void setBlockData(@TransformParam IBlockData iBlockData);

    @CastTo(IBlockData.class)
    IBlockData getBlockData();
}
