package xyz.acrylicstyle.region.api.block.state.types;

import util.reflect.Ref;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

public enum EnumHalf implements EnumNMS {
    TOP, BOTTOM;

    private final Object nms;

    EnumHalf() {
        this.nms = Ref.forName(ReflectionUtil.getNMSPackage() + ".BlockPropertyHalf").getField(this.name()).get(null);
    }

    @Override
    public Object getNMS() { return nms; }
}
