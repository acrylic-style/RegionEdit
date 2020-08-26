package xyz.acrylicstyle.region.api.block.state.types;

import util.reflect.Ref;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

public enum EnumStairsShape implements EnumNMS {
    STRAIGHT, INNER_RIGHT, INNER_LEFT, OUTER_RIGHT, OUTER_LEFT;

    private final Object nms;

    EnumStairsShape() {
        this.nms = Ref.forName(ReflectionUtil.getNMSPackage() + ".BlockPropertyStairsShape").getField(this.name()).get(null);
    }

    @Override
    public Object getNMS() { return nms; }
}
