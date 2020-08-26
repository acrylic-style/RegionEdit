package xyz.acrylicstyle.region.api.block.state.types;

import util.reflect.Ref;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

public enum EnumDirection implements EnumNMS {
    UP,
    DOWN,
    EAST,
    NORTH,
    SOUTH,
    WEST,
    ;

    private final Object nms;

    EnumDirection() {
        this.nms = Ref.forName(ReflectionUtil.getNMSPackage() + ".EnumDirection").getField(this.name()).get(null);
    }

    @Override
    public Object getNMS() { return nms; }
}
