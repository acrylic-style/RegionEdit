package xyz.acrylicstyle.region.api.block.state.types;

import util.reflect.Ref;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

public enum EnumBambooSize implements EnumNMS {
    NONE, SMALL, LARGE;

    private final Object nms;

    EnumBambooSize() {
        this.nms = Ref.forName(ReflectionUtil.getNMSPackage() + ".BlockPropertyBambooSize").getField(this.name()).get(null);
    }

    @Override
    public Object getNMS() { return nms; }
}
