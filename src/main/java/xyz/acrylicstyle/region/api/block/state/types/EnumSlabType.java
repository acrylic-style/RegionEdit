package xyz.acrylicstyle.region.api.block.state.types;

import util.reflect.Ref;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

public enum EnumSlabType implements EnumNMS {
    TOP, BOTTOM, DOUBLE;

    private final Object nms;

    EnumSlabType() {
        this.nms = Ref.forName(ReflectionUtil.getNMSPackage() + ".BlockPropertySlabType").getField(this.name()).get(null);
    }

    @Override
    public Object getNMS() { return nms; }
}
