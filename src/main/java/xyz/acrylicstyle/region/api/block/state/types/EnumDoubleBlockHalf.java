package xyz.acrylicstyle.region.api.block.state.types;

import util.reflect.Ref;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

public enum EnumDoubleBlockHalf implements EnumNMS {
    LOWER,
    UPPER;

    private final Object nms;

    EnumDoubleBlockHalf() {
        this.nms = Ref.forName(ReflectionUtil.getNMSPackage() + ".BlockPropertyDoubleBlockHalf").getField(this.name()).get(null);
    }

    @Override
    public Object getNMS() { return nms; }
}
