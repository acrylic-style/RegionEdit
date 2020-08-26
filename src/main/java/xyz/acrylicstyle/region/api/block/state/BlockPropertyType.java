package xyz.acrylicstyle.region.api.block.state;

import org.jetbrains.annotations.NotNull;
import util.ICollectionList;
import xyz.acrylicstyle.region.api.block.state.types.EnumAxis;
import xyz.acrylicstyle.region.api.block.state.types.EnumBambooSize;
import xyz.acrylicstyle.region.api.block.state.types.EnumBedPart;
import xyz.acrylicstyle.region.api.block.state.types.EnumBellFace;
import xyz.acrylicstyle.region.api.block.state.types.EnumBlockFace;
import xyz.acrylicstyle.region.api.block.state.types.EnumChestType;
import xyz.acrylicstyle.region.api.block.state.types.EnumDirection;
import xyz.acrylicstyle.region.api.block.state.types.EnumDoubleBlockHalf;
import xyz.acrylicstyle.region.api.block.state.types.EnumLR;
import xyz.acrylicstyle.region.api.block.state.types.EnumNMS;
import xyz.acrylicstyle.region.api.block.state.types.EnumPistonType;
import xyz.acrylicstyle.region.api.block.state.types.EnumHalf;
import xyz.acrylicstyle.region.api.block.state.types.EnumRedstoneDirection;
import xyz.acrylicstyle.region.api.block.state.types.EnumSlabType;
import xyz.acrylicstyle.region.api.block.state.types.EnumStairsShape;

import java.util.function.Function;

public class BlockPropertyType<T> {
    public static final BlockPropertyType<String> STRING = new BlockPropertyType<>(String.class, Function.identity());
    public static final BlockPropertyType<Boolean> BOOLEAN = new BlockPropertyType<>(boolean.class, Boolean::parseBoolean);
    public static final BlockPropertyType<Integer> INTEGER = new BlockPropertyType<>(int.class, Integer::parseInt);
    public static final BlockPropertyType<EnumAxis> AXIS = new BlockPropertyType<>(EnumAxis.class, s -> EnumAxis.valueOf(s.toUpperCase()));
    public static final BlockPropertyType<EnumSlabType> SLAB = new BlockPropertyType<>(EnumSlabType.class, s -> EnumSlabType.valueOf(s.toUpperCase()));
    public static final BlockPropertyType<EnumHalf> HALF = new BlockPropertyType<>(EnumHalf.class, s -> EnumHalf.valueOf(s.toUpperCase()));
    public static final BlockPropertyType<EnumChestType> CHEST = new BlockPropertyType<>(EnumChestType.class, s -> EnumChestType.valueOf(s.toUpperCase()));
    public static final BlockPropertyType<EnumDirection> DIRECTION = new BlockPropertyType<>(EnumDirection.class, s -> EnumDirection.valueOf(s.toUpperCase()));
    public static final BlockPropertyType<EnumBlockFace> BLOCK_FACE = new BlockPropertyType<>(EnumBlockFace.class, s -> EnumBlockFace.valueOf(s.toUpperCase()));
    public static final BlockPropertyType<EnumLR> LEFT_RIGHT = new BlockPropertyType<>(EnumLR.class, s -> EnumLR.valueOf(s.toUpperCase()));
    public static final BlockPropertyType<EnumDoubleBlockHalf> DOUBLE_HALF = new BlockPropertyType<>(EnumDoubleBlockHalf.class, s -> EnumDoubleBlockHalf.valueOf(s.toUpperCase()));
    public static final BlockPropertyType<EnumStairsShape> STAIRS_SHAPE = new BlockPropertyType<>(EnumStairsShape.class, s -> EnumStairsShape.valueOf(s.toUpperCase()));
    public static final BlockPropertyType<EnumRedstoneDirection> REDSTONE_DIRECTION = new BlockPropertyType<>(EnumRedstoneDirection.class, s -> EnumRedstoneDirection.valueOf(s.toUpperCase()));
    public static final BlockPropertyType<EnumPistonType> PISTON = new BlockPropertyType<>(EnumPistonType.class, s -> EnumPistonType.valueOf(s.toUpperCase()));
    public static final BlockPropertyType<EnumBellFace> BELL = new BlockPropertyType<>(EnumBellFace.class, s -> EnumBellFace.valueOf(s.toUpperCase()));
    public static final BlockPropertyType<EnumBambooSize> BAMBOO = new BlockPropertyType<>(EnumBambooSize.class, s -> EnumBambooSize.valueOf(s.toUpperCase()));
    public static final BlockPropertyType<EnumBedPart> BED_PART = new BlockPropertyType<>(EnumBedPart.class, s -> EnumBedPart.valueOf(s.toUpperCase()));

    @NotNull private final Class<T> clazz;
    @NotNull private final Function<String, T> parser;
    private final boolean nmsAble;

    private BlockPropertyType(@NotNull Class<T> clazz, @NotNull Function<String, T> parser) {
        this.clazz = clazz;
        this.parser = parser;
        this.nmsAble = ICollectionList.asList(clazz.getInterfaces()).contains(EnumNMS.class);
    }

    @NotNull
    public Class<T> getClazz() { return clazz; }

    @NotNull
    public Function<String, T> getParser() {
        return parser;
    }

    public boolean isNMSable() { return nmsAble; }

    public EnumNMS parseNMSable(String s) {
        try {
            return (EnumNMS) parser.apply(s);
        } catch (RuntimeException e) {
            throw new RuntimeException("Couldn't parse " + s, e);
        }
    }

    public T parse(String s) {
        return parser.apply(s);
    }

    public Object get(String s) {
        T t = parser.apply(s);
        if (t instanceof EnumNMS) return ((EnumNMS) t).getNMS();
        return t;
    }
}
