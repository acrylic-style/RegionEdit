package xyz.acrylicstyle.region.api.block.state;

import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockState {
    @NotNull private final Material type;
    private final byte data;
    @Nullable private final BlockStatePropertyMap propertyMap;
    private final Location location;

    public BlockState(@NotNull Material material, byte data, @Nullable BlockStatePropertyMap propertyMap) {
        this(material, data, propertyMap, null);
    }

    public BlockState(@NotNull Material material, byte data, @Nullable BlockStatePropertyMap propertyMap, @Nullable Location location) {
        this.type = material;
        this.data = data;
        this.propertyMap = propertyMap;
        this.location = location;
    }

    public Location getLocation() { return location; }

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
}
