package xyz.acrylicstyle.region.api.block.state.types;

import util.reflect.Ref;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

public enum EnumBellFace implements EnumNMS {
    FLOOR,
    CEILING,
    SINGLE_WALL,
    DOUBLE_WALL;

    private final Object nms;

    EnumBellFace() {
        this.nms = Ref.forName(ReflectionUtil.getNMSPackage() + ".BlockPropertyBellAttach").getField(this.name()).get(null);
    }

    @Override
    public Object getNMS() { return nms; }
}
