package xyz.acrylicstyle.region.api.block.state.types;

import util.reflect.Ref;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

public enum EnumBlockFace implements EnumNMS {
    CEILING,
    WALL,
    FLOOR;

    private final Object nms;

    EnumBlockFace() {
        this.nms = Ref.forName(ReflectionUtil.getNMSPackage() + ".BlockPropertyAttachPosition").getField(this.name()).get(null);
    }

    @Override
    public Object getNMS() { return nms; }
}
