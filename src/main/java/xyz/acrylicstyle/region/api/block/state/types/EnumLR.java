package xyz.acrylicstyle.region.api.block.state.types;

import util.reflect.Ref;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

public enum EnumLR implements EnumNMS {
    LEFT,
    RIGHT;

    private final Object nms;

    EnumLR() {
        this.nms = Ref.forName(ReflectionUtil.getNMSPackage() + ".BlockPropertyDoorHinge").getField(this.name()).get(null);
    }

    @Override
    public Object getNMS() { return nms; }
}
