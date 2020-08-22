package xyz.acrylicstyle.region.api.block.state;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.region.api.block.state.types.EnumAxis;
import xyz.acrylicstyle.region.api.block.state.types.EnumBlockFace;
import xyz.acrylicstyle.region.api.block.state.types.EnumChestType;
import xyz.acrylicstyle.region.api.block.state.types.EnumDirection;
import xyz.acrylicstyle.region.api.block.state.types.EnumDoorHalf;
import xyz.acrylicstyle.region.api.block.state.types.EnumLR;
import xyz.acrylicstyle.region.api.block.state.types.EnumRedStone;
import xyz.acrylicstyle.region.api.block.state.types.EnumSlabType;
import xyz.acrylicstyle.region.api.block.state.types.EnumStairsShape;

import java.util.function.Function;

public class BlockPropertyType<T> {
    @SuppressWarnings("unused") // it's here just to allow raw access to map
    public static final BlockPropertyType<String> STRING = new BlockPropertyType<>(String.class, Function.identity());
    public static final BlockPropertyType<Boolean> BOOLEAN = new BlockPropertyType<>(boolean.class, Boolean::parseBoolean);
    public static final BlockPropertyType<Integer> INTEGER = new BlockPropertyType<>(int.class, Integer::parseInt);
    public static final BlockPropertyType<EnumAxis> AXIS = new BlockPropertyType<>(EnumAxis.class, s -> EnumAxis.valueOf(s.toUpperCase()));
    public static final BlockPropertyType<EnumSlabType> SLAB = new BlockPropertyType<>(EnumSlabType.class, s -> EnumSlabType.valueOf(s.toUpperCase()));
    public static final BlockPropertyType<EnumChestType> CHEST = new BlockPropertyType<>(EnumChestType.class, s -> EnumChestType.valueOf(s.toUpperCase()));
    public static final BlockPropertyType<EnumDirection> DIRECTION = new BlockPropertyType<>(EnumDirection.class, s -> EnumDirection.valueOf(s.toUpperCase()));
    public static final BlockPropertyType<EnumBlockFace> BLOCK_FACE = new BlockPropertyType<>(EnumBlockFace.class, s -> EnumBlockFace.valueOf(s.toUpperCase()));
    public static final BlockPropertyType<EnumLR> LEFT_RIGHT = new BlockPropertyType<>(EnumLR.class, s -> EnumLR.valueOf(s.toUpperCase()));
    public static final BlockPropertyType<EnumDoorHalf> DOOR_HALF = new BlockPropertyType<>(EnumDoorHalf.class, s -> EnumDoorHalf.valueOf(s.toUpperCase()));
    public static final BlockPropertyType<EnumStairsShape> SHAPE = new BlockPropertyType<>(EnumStairsShape.class, s -> EnumStairsShape.valueOf(s.toUpperCase()));
    public static final BlockPropertyType<EnumRedStone.EnumDirection> REDSTONE_DIRECTION = new BlockPropertyType<>(EnumRedStone.EnumDirection.class, s -> EnumRedStone.EnumDirection.valueOf(s.toUpperCase()));

    @NotNull private final Class<T> clazz;
    @NotNull private final Function<String, T> parser;

    private BlockPropertyType(@NotNull Class<T> clazz, @NotNull Function<String, T> parser) {
        this.clazz = clazz;
        this.parser = parser;
    }

    @NotNull
    public Class<T> getClazz() { return clazz; }

    @NotNull
    public Function<String, T> getParser() {
        return parser;
    }

    public T parse(String s) {
        return parser.apply(s);
    }
}
