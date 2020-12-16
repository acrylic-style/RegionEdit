package xyz.acrylicstyle.region.api.block.state;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.CollectionList;
import util.ICollectionList;
import util.reflect.Ref;
import xyz.acrylicstyle.tomeito_api.utils.Log;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

import java.util.Objects;

// see nms.BlockProperties for implementing this
// todo: add more things
// todo: if something throws exception "xxxx has missing property" then add the missing property key here
// todo: incompatible versions
public enum EnumBlockPropertyKey {
    SNOWY("snowy", BlockPropertyType.BOOLEAN, blockProperties("z")),
    PERSISTENT("persistent", BlockPropertyType.BOOLEAN, blockProperties("v")),
    DISTANCE("distance", BlockPropertyType.INTEGER, blockProperties("an")),
    DISTANCE0("distance", BlockPropertyType.INTEGER, blockProperties("aB")),
    AXIS_XZ("axis", BlockPropertyType.AXIS, blockProperties("E")),
    AXIS_XYZ("axis", BlockPropertyType.AXIS, blockProperties("F")),
    BITES("bites", BlockPropertyType.INTEGER, blockProperties("al")),
    ROTATION("rotation", BlockPropertyType.INTEGER, blockProperties("aD")),
    WATERLOGGED("waterlogged", BlockPropertyType.BOOLEAN, blockProperties("C")),
    OCCUPIED("occupied", BlockPropertyType.BOOLEAN, blockProperties("t")),
    PART("part", BlockPropertyType.BED_PART, blockProperties("aE")),
    SIGNAL_FIRE("signal_fire", BlockPropertyType.BOOLEAN, blockProperties("y")),

    SLAB_TYPE("type", BlockPropertyType.SLAB, blockProperties("aK")),
    CHEST_TYPE("type", BlockPropertyType.CHEST, blockProperties("aF")),
    PISTON_TYPE("type", BlockPropertyType.PISTON, blockProperties("aJ")),

    // direction
    EAST("east", BlockPropertyType.BOOLEAN, blockProperties("J")),
    NORTH("north", BlockPropertyType.BOOLEAN, blockProperties("I")),
    SOUTH("south", BlockPropertyType.BOOLEAN, blockProperties("K")),
    WEST("west", BlockPropertyType.BOOLEAN, blockProperties("L")),
    UP("up", BlockPropertyType.BOOLEAN, blockProperties("G")),
    DOWN("down", BlockPropertyType.BOOLEAN, blockProperties("H")),

    AGE_1("age", BlockPropertyType.INTEGER, blockProperties("ae")),
    AGE_2("age", BlockPropertyType.INTEGER, blockProperties("af")),
    AGE_3("age", BlockPropertyType.INTEGER, blockProperties("ag")),
    AGE_5("age", BlockPropertyType.INTEGER, blockProperties("ah")),
    AGE_7("age", BlockPropertyType.INTEGER, blockProperties("ai")),
    AGE_15("age", BlockPropertyType.INTEGER, blockProperties("aj")),
    AGE_25("age", BlockPropertyType.INTEGER, blockProperties("ak")),

    EYE("eye", BlockPropertyType.BOOLEAN, blockProperties("h")),
    LEAVES("leaves", BlockPropertyType.BAMBOO, blockProperties("aN")),

    FACING_ALL("facing", BlockPropertyType.DIRECTION, blockProperties("M")), // button, etc
    FACING_NO_UP("facing", BlockPropertyType.DIRECTION, blockProperties("N")),
    FACING_HORIZONTAL("facing", BlockPropertyType.DIRECTION, blockProperties("O")), // campfire

    POWERED("powered", BlockPropertyType.BOOLEAN, blockProperties("w")),
    FACE("face", BlockPropertyType.BLOCK_FACE, blockProperties("Q")),
    OPEN("open", BlockPropertyType.BOOLEAN, blockProperties("u")),
    HINGE("hinge", BlockPropertyType.LEFT_RIGHT, blockProperties("aH")),

    DOUBLE_HALF("half", BlockPropertyType.DOUBLE_HALF, blockProperties("aa")),
    HALF("half", BlockPropertyType.HALF, blockProperties("ab")),

    POWER("power", BlockPropertyType.INTEGER, blockProperties("az")),
    LIT("lit", BlockPropertyType.BOOLEAN, blockProperties("r")),
    SHAPE("shape", BlockPropertyType.STAIRS_SHAPE, blockProperties("aL")),
    EXTENDED("extended", BlockPropertyType.BOOLEAN, blockProperties("g")),
    ENABLED("enabled", BlockPropertyType.BOOLEAN, blockProperties("f")),
    CONDITIONAL("conditional", BlockPropertyType.BOOLEAN, blockProperties("c")),
    INVERTED("inverted", BlockPropertyType.BOOLEAN, blockProperties("p")),

    LEVEL_0_3("level", BlockPropertyType.INTEGER, blockProperties("ar")),
    LEVEL_0_8("level", BlockPropertyType.INTEGER, blockProperties("as")),
    LEVEL_1_8("level", BlockPropertyType.INTEGER, blockProperties("at")),
    LEVEL_0_15("level", BlockPropertyType.INTEGER, blockProperties("av")),

    LAYERS("layers", BlockPropertyType.INTEGER, blockProperties("aq")),
    HATCH("hatch", BlockPropertyType.INTEGER, blockProperties("ap")),
    HONEY_LEVEL("honey_level", BlockPropertyType.INTEGER, blockProperties("au")),
    EGGS("eggs", BlockPropertyType.INTEGER, blockProperties("ao")),
    STAGE("stage", BlockPropertyType.INTEGER, blockProperties("aA")),
    ATTACHMENT("attachment", BlockPropertyType.BELL, blockProperties("R")),

    // repeater
    DELAY("delay", BlockPropertyType.INTEGER, blockProperties("am")),
    LOCKED("locked", BlockPropertyType.BOOLEAN, blockProperties("s")),

    // direction (redstone)
    REDSTONE_EAST("east", BlockPropertyType.REDSTONE_DIRECTION, blockProperties("W")),
    REDSTONE_NORTH("north", BlockPropertyType.REDSTONE_DIRECTION, blockProperties("X")),
    REDSTONE_SOUTH("south", BlockPropertyType.REDSTONE_DIRECTION, blockProperties("Y")),
    REDSTONE_WEST("west", BlockPropertyType.REDSTONE_DIRECTION, blockProperties("Z")),

    // respawn anchor
    CHARGES("charges", BlockPropertyType.INTEGER, blockProperties("aC")),
    ;

    @NotNull private final String name;
    @NotNull private final BlockPropertyType<?> valueType;
    private final Object blockProperty;

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

    private static Object blockProperties(String field) {
        return Ref.forName(ReflectionUtil.getNMSPackage() + ".BlockProperties").getField(field).accessible(true).get(null);
    }

    private static CollectionList<EnumBlockPropertyKey> cachedValues;
    private static CollectionList<EnumBlockPropertyKey> getValues() {
        if (cachedValues != null) return cachedValues;
        cachedValues = ICollectionList.asList(values());
        return cachedValues;
    }

    // todo: add mapping (switch statement) if thrown error like "xxxx has missing mapping!" or "Could not set block data"
    @Nullable
    public static EnumBlockPropertyKey getProperty(@NotNull String id, @NotNull String s) {
        CollectionList<EnumBlockPropertyKey> list = getValues().filter(prop -> prop.name.equals(s));
        if (list.size() == 1) return Objects.requireNonNull(list.first());
        if (list.size() == 0) {
            Log.warn("Parsing " + id + ": " + id + " has missing property!");
            return null;
        }
        switch (s) {
            case "level":
                if (id.contains("lava") || id.contains("water")) {
                    return LEVEL_0_15;
                } else if (id.contains("cauldron")) {
                    return LEVEL_0_3;
                }
            case "age":
                if (id.contains("cocoa")) {
                    return AGE_2;
                } else if (id.contains("sweet_berry_bush")) {
                    return AGE_3;
                } else if (id.contains("sugar_cane") || id.contains("cactus")) {
                    return AGE_15;
                }
            case "distance":
                if (id.contains("leaves")) {
                    return DISTANCE;
                }
            case "axis":
                if (id.contains("_log") || id.contains("bone_block")) {
                    return AXIS_XYZ;
                }
            case "facing":
                if (id.contains("campfire") || id.contains("cocoa") || id.contains("bed")) {
                    return FACING_HORIZONTAL;
                } else {
                    return FACING_ALL;
                }
            case "half":
                if (id.contains("_door")
                        || id.contains("tall_seagrass")
                        || id.contains("large_fern")
                        || id.contains("peony")
                        || id.contains("tall_grass")) {
                    return DOUBLE_HALF;
                } else {
                    return HALF;
                }
            case "type":
                if (id.contains("slab")) {
                    return SLAB_TYPE;
                } else if (id.contains("chest")) {
                    return CHEST_TYPE;
                } else if (id.contains("piston")) {
                    return PISTON_TYPE;
                }
            case "east":
                if (id.contains("redstone")) {
                    return REDSTONE_EAST;
                } else {
                    return EAST;
                }
            case "north":
                if (id.contains("redstone")) {
                    return REDSTONE_NORTH;
                } else {
                    return NORTH;
                }
            case "south":
                if (id.contains("redstone")) {
                    return REDSTONE_SOUTH;
                } else {
                    return SOUTH;
                }
            case "west":
                if (id.contains("redstone")) {
                    return REDSTONE_WEST;
                } else {
                    return WEST;
                }
            default: {
                Log.warn("Parsing " + id + ": " + s + " has missing mapping!");
                return null;
            }
        }
    }
}
