package xyz.acrylicstyle.region.api.block.state.types;

import util.reflect.Ref;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

public enum EnumChestType implements EnumNMS {
    SINGLE,
    LEFT,
    RIGHT;

    private final Object nms;

    EnumChestType() {
        this.nms = Ref.forName(ReflectionUtil.getNMSPackage() + ".BlockPropertyChestType").getField(this.name()).get(null);
    }

    @Override
    public Object getNMS() { return nms; }
}
