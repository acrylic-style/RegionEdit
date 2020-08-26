package xyz.acrylicstyle.region.api.block.state.types;

import util.reflect.Ref;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

public enum EnumBedPart implements EnumNMS {
    HEAD,
    FOOT;

    private final Object nms;

    EnumBedPart() {
        this.nms = Ref.forName(ReflectionUtil.getNMSPackage() + ".BlockPropertyBedPart").getField(this.name()).get(null);
    }

    @Override
    public Object getNMS() { return nms; }
}
