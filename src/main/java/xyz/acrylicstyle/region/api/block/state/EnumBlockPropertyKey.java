package xyz.acrylicstyle.region.api.block.state;

import org.jetbrains.annotations.NotNull;
import util.reflect.Ref;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

// see nms.BlockProperties
// how to set property: see nms.BlockLeaves (Line 14:9)
public enum EnumBlockPropertyKey {
    SNOWY("snowy", BlockPropertyType.BOOLEAN, blockStateBoolean("snowy")),
    PERSISTENT("persistent", BlockPropertyType.BOOLEAN, blockStateBoolean("persistent")),
    DISTANCE("distance", BlockPropertyType.INTEGER),
    AXIS("axis", BlockPropertyType.AXIS),
    BITES("bites", BlockPropertyType.INTEGER),
    ROTATION("rotation", BlockPropertyType.INTEGER),
    WATERLOGGED("waterlogged", BlockPropertyType.BOOLEAN, blockStateBoolean("waterlogged")),
    SLAB_TYPE("type", BlockPropertyType.SLAB),
    CHEST_TYPE("type", BlockPropertyType.CHEST),

    // direction
    EAST("east", BlockPropertyType.BOOLEAN),
    NORTH("north", BlockPropertyType.BOOLEAN),
    SOUTH("south", BlockPropertyType.BOOLEAN),
    WEST("west", BlockPropertyType.BOOLEAN),
    UP("up", BlockPropertyType.BOOLEAN),
    DOWN("down", BlockPropertyType.BOOLEAN),

    AGE("age", BlockPropertyType.INTEGER),
    EYE("eye", BlockPropertyType.BOOLEAN, blockStateBoolean("eye")),
    FACING("facing", BlockPropertyType.DIRECTION),
    POWERED("powered", BlockPropertyType.BOOLEAN, blockStateBoolean("powered")),
    FACE("face", BlockPropertyType.BLOCK_FACE),
    OPEN("open", BlockPropertyType.BOOLEAN, blockStateBoolean("open")),
    HINGE("hinge", BlockPropertyType.LEFT_RIGHT),
    DOOR_HALF("half", BlockPropertyType.DOOR_HALF),
    POWER("power", BlockPropertyType.INTEGER),
    LIT("lit", BlockPropertyType.BOOLEAN, blockStateBoolean("lit")),
    SHAPE("shape", BlockPropertyType.SHAPE),
    STAIRS_HALF("half", BlockPropertyType.SLAB),
    EXTENDED("extended", BlockPropertyType.BOOLEAN, blockStateBoolean("extended")),
    ENABLED("enabled", BlockPropertyType.BOOLEAN, blockStateBoolean("enabled")),
    CONDITIONAL("conditional", BlockPropertyType.BOOLEAN, blockStateBoolean("conditional")),
    LEVEL("level", BlockPropertyType.INTEGER),
    INVERTED("inverted", BlockPropertyType.BOOLEAN, blockStateBoolean("inverted")),

    // repeater
    DELAY("delay", BlockPropertyType.INTEGER),
    LOCKED("locked", BlockPropertyType.BOOLEAN, blockStateBoolean("locked")),

    // direction (redstone)
    REDSTONE_EAST("east", BlockPropertyType.REDSTONE_DIRECTION),
    REDSTONE_NORTH("north", BlockPropertyType.REDSTONE_DIRECTION),
    REDSTONE_SOUTH("south", BlockPropertyType.REDSTONE_DIRECTION),
    REDSTONE_WEST("west", BlockPropertyType.REDSTONE_DIRECTION),
    ;

    @NotNull private final String name;
    @NotNull private final BlockPropertyType<?> valueType;
    private final Object blockProperty;

    EnumBlockPropertyKey(@NotNull String name, @NotNull BlockPropertyType<?> valueType) {
        this(name, valueType, null);
    }

    EnumBlockPropertyKey(@NotNull String name, @NotNull BlockPropertyType<?> valueType, Object blockProperty) {
        this.name = name;
        this.valueType = valueType;
        this.blockProperty = blockProperty;
    }

    public Object getBlockProperty() { return blockProperty; }

    @NotNull
    public BlockPropertyType<?> getValueType() { return valueType; }

    @NotNull
    public String getName() { return name; }

    public static Object blockStateBoolean(String name) {
        return Ref.forName(ReflectionUtil.getNMSPackage() + ".BlockStateBoolean").getMethod("of").invoke(null, name);
    }
}
