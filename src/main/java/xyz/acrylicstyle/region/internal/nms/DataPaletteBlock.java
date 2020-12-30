package xyz.acrylicstyle.region.internal.nms;

import org.jetbrains.annotations.Contract;
import util.ActionableResult;
import util.reflect.RefClass;
import util.reflect.RefMethod;
import xyz.acrylicstyle.shared.NMSAPI;

public class DataPaletteBlock extends NMSAPI {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final RefClass<Object> CLASS = new RefClass(getClassWithoutException("DataPaletteBlock"));
    public static final RefMethod<Object> setBlockIndex = ActionableResult.ofThrowable(() -> CLASS
            .getDeclaredMethod("setBlockIndex", int.class, getClassWithoutException("IBlockData"))
            .accessible(true)).orElseGet(() -> CLASS
            .getDeclaredMethod("setBlockIndex", int.class, Object.class)
            .accessible(true));
    public static final RefMethod<Object> getType = ActionableResult.ofThrowable(() -> CLASS
            .getDeclaredMethod("a", int.class, int.class, int.class)
            .accessible(true)).nullableValue();

    @Contract("null -> fail")
    public DataPaletteBlock(Object o) {
        super(o, "DataPaletteBlock");
    }

    public Object getType(int x, int y, int z) {
        if (getType == null) return null;
        return getType.invoke(this.o, x, y, z);
    }

    public void setBlock(int x, int y, int z, Object iBlockData) {
        setBlockIndex(pos(x, y, z), iBlockData);
    }

    public void setBlockIndex(int i, Object iBlockData) {
        setBlockIndex.invoke(this.o, i, iBlockData);
    }

    @Contract(pure = true)
    public int pos(int x, int y, int z) {
        return y << 8 | z << 4 | x;
    }
}
