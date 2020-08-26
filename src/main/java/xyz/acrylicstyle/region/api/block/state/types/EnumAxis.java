package xyz.acrylicstyle.region.api.block.state.types;

import util.reflect.Ref;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

public enum EnumAxis implements EnumNMS {
    X, Y, Z;

    private final Object nms;

    EnumAxis() {
        this.nms = Ref.forName(ReflectionUtil.getNMSPackage() + ".EnumDirection$EnumAxis").getField(this.name()).get(null);
    }

    @Override
    public Object getNMS() { return nms; }
}
