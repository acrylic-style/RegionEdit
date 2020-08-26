package xyz.acrylicstyle.region.api.block.state.types;

import util.reflect.Ref;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

public enum EnumRedstoneDirection implements EnumNMS {
    SIDE,
    NONE,
    UP;

    private final Object nms;

    EnumRedstoneDirection() {
        this.nms = Ref.forName(ReflectionUtil.getNMSPackage() + ".BlockPropertyRedstoneSide").getField(this.name()).get(null);
    }

    @Override
    public Object getNMS() {
        return nms;
    }
}
