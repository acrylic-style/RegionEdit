package xyz.acrylicstyle.region.api.block.state.types;

import util.reflect.Ref;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

public enum EnumPistonType implements EnumNMS {
    DEFAULT,
    STICKY;

    private final Object nms;

    EnumPistonType() {
        this.nms = Ref.forName(ReflectionUtil.getNMSPackage() + ".BlockPropertyPistonType").getField(this.name()).get(null);
    }

    @Override
    public Object getNMS() { return nms; }
}
