package xyz.acrylicstyle.region.api.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.region.internal.utils.Reflection;

public class Block {
    private final Location location;
    private final Material type;
    private final byte data;

    public Block(@NotNull org.bukkit.block.Block block) {
        this.location = block.getLocation();
        this.type = block.getType();
        this.data = Reflection.getData(block);
    }

    public Block(@NotNull Location location, @NotNull Material type, byte data) {
        this.location = location;
        this.type = type;
        this.data = data;
    }

    public Location getLocation() {
        return location;
    }

    public Material getType() {
        return type;
    }

    public byte getData() {
        return data;
    }

    @Override
    public int hashCode() {
        return this.location.getBlockX() * this.location.getBlockY() * this.location.getBlockZ() * getType().ordinal() * getData();
    }
}
