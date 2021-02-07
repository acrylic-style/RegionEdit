package xyz.acrylicstyle.region.api.reflector.org.bukkit.entity;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import util.reflector.Reflector;
import util.reflector.ReflectorHandler;
import util.reflector.TransformParam;
import util.reflector.Type;
import xyz.acrylicstyle.region.api.reflector.org.bukkit.craftbukkit.v1_13.block.data.CraftBlockData;

public interface BukkitPlayer extends Player {
    @NotNull
    static BukkitPlayer getInstance(@NotNull Player player) {
        return Reflector.newReflector(null, BukkitPlayer.class, new ReflectorHandler(Player.class, player));
    }

    void sendBlockChange(Location location, @Type("org.bukkit.block.data.BlockData") @TransformParam CraftBlockData blockData);
}
