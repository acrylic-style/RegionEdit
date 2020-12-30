package xyz.acrylicstyle.region.api.block.state;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.ReflectionHelper;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.util.BlockPos;
import xyz.acrylicstyle.region.api.util.Tuple;

import java.io.Serializable;

public class BlockState implements Serializable {
    @NotNull protected final Material type;
    protected final byte data;
    @Nullable private final BlockStatePropertyMap propertyMap;
    protected final Tuple<Integer, Integer, Integer> location;

    private static boolean checkBlock_getData() {
        return ReflectionHelper.findMethod(Block.class, "getData") != null;
    }

    protected BlockState(@NotNull BlockState blockState) {
        this.type = blockState.type;
        this.data = blockState.data;
        this.propertyMap = blockState.propertyMap;
        this.location = blockState.location;
    }

    public BlockState(@NotNull BlockState blockState, Tuple<Integer, Integer, Integer> location) {
        this.type = blockState.type;
        this.data = blockState.data;
        this.propertyMap = blockState.propertyMap;
        this.location = location;
    }

    public BlockState(@NotNull xyz.acrylicstyle.region.api.block.Block block) {
        this(block.getBukkitBlock());
    }

    @SuppressWarnings("deprecation")
    public BlockState(@NotNull Block block) {
        this(block.getType(), checkBlock_getData() ? block.getData() : 0, BlockStatePropertyMap.from(block), block.getLocation());
    }

    public BlockState(@NotNull Material material, byte data, @Nullable BlockStatePropertyMap propertyMap) {
        this(material, data, propertyMap, (Tuple<Integer, Integer, Integer>) null);
    }

    public BlockState(@NotNull Material material, byte data, @Nullable BlockStatePropertyMap propertyMap, @Nullable Location location) {
        this(material, data, propertyMap, location == null ? null : new Tuple<>(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }

    public BlockState(@NotNull Material material, byte data, @Nullable BlockStatePropertyMap propertyMap, @Nullable Tuple<Integer, Integer, Integer> location) {
        this.type = material;
        this.data = data;
        this.propertyMap = propertyMap;
        this.location = location;
    }

    public Tuple<Integer, Integer, Integer> getLocation() { return location; }

    public BlockPos getBlockPos(World world) { return new BlockPos(world, location); }

    @NotNull
    public Material getType() { return type; }

    public byte getData() { return data; }

    @Nullable
    public BlockStatePropertyMap getPropertyMap() { return propertyMap; }

    @Override
    public String toString() {
        return "BlockState{" + "type=" + type +
                ", data=" + data +
                ", propertyMap=" + propertyMap +
                ", location=" + location +
                '}';
    }

    @SuppressWarnings("deprecation")
    public void update(@NotNull World world) {
        BlockPos pos = getBlockPos(world);
        pos.getBlock().setType(type);
        if (data != 0) pos.getBlock().setData(data);
        if (propertyMap != null) propertyMap.apply(pos.getBlock());
    }

    @NotNull
    public BlockState implementMethods() {
        return RegionEdit.getInstance().implementMethods(this);
    }

    public void updateFast(@NotNull World world) { implementMethods().updateFast(world); }
}
