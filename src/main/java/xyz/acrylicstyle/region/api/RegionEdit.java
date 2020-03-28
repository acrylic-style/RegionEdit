package xyz.acrylicstyle.region.api;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import util.CollectionList;
import util.ICollectionList;
import xyz.acrylicstyle.region.api.manager.HistoryManager;
import xyz.acrylicstyle.region.api.player.UserSession;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public interface RegionEdit extends Plugin {
    @NotNull
    static RegionEdit getInstance() {
        RegisteredServiceProvider<RegionEdit> service = Bukkit.getServicesManager().getRegistration(RegionEdit.class);
        if (service == null) throw new NullPointerException();
        return service.getProvider();
    }

    @NotNull
    static CollectionList<Block> getBlocks(@NotNull Location loc1, @NotNull Location loc2, Material block, Function<Block, Boolean> filterFunction) {
        if (!loc1.getWorld().equals(loc2.getWorld())) throw new RuntimeException("Cannot compare between worlds");
        List<Block> blocks = new ArrayList<>();
        int x1, x2, y1, y2, z1, z2;
        x1 = loc1.getX() > loc2.getX() ? (int) loc2.getX() : (int) loc1.getX();
        y1 = loc1.getY() > loc2.getY() ? (int) loc2.getY() : (int) loc1.getY();
        z1 = loc1.getZ() > loc2.getZ() ? (int) loc2.getZ() : (int) loc1.getZ();

        x2 = ((int) loc1.getX()) == x1 ? (int) loc2.getX() : (int) loc1.getX();
        y2 = ((int) loc1.getY()) == y1 ? (int) loc2.getY() : (int) loc1.getY();
        z2 = ((int) loc1.getZ()) == z1 ? (int) loc2.getZ() : (int) loc1.getZ();
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    Block b = loc1.getWorld().getBlockAt(x, y, z);
                    if (block == null) {
                        if (filterFunction != null) {
                            if (filterFunction.apply(b)) blocks.add(b);
                        } else blocks.add(b);
                    } else {
                        if (b.getType() == block && filterFunction.apply(b)) blocks.add(b);
                    }
                }
            }
        }
        return ICollectionList.asList(blocks);
    }

    @NotNull
    static CollectionList<Block> getBlocksInvert(@NotNull Location loc1, @NotNull Location loc2, Material block) {
        if (!loc1.getWorld().equals(loc2.getWorld())) throw new RuntimeException("Cannot compare between worlds");
        List<Block> blocks = new ArrayList<>();
        int x1, x2, y1, y2, z1, z2;
        x1 = loc1.getX() > loc2.getX() ? (int) loc2.getX() : (int) loc1.getX();
        y1 = loc1.getY() > loc2.getY() ? (int) loc2.getY() : (int) loc1.getY();
        z1 = loc1.getZ() > loc2.getZ() ? (int) loc2.getZ() : (int) loc1.getZ();

        x2 = ((int) loc1.getX()) == x1 ? (int) loc2.getX() : (int) loc1.getX();
        y2 = ((int) loc1.getY()) == y1 ? (int) loc2.getY() : (int) loc1.getY();
        z2 = ((int) loc1.getZ()) == z1 ? (int) loc2.getZ() : (int) loc1.getZ();
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    Block b = loc1.getWorld().getBlockAt(x, y, z);
                    if (b.getType() != block) blocks.add(b);
                }
            }
        }
        return ICollectionList.asList(blocks);
    }

    @NotNull
    static CollectionList<Block> getNearbyBlocks(@NotNull Location location, int radius) {
        CollectionList<Block> blocks = new CollectionList<>();
        for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }
    @NotNull
    UserSession getUserSession(@NotNull UUID uuid);

    @NotNull
    default UserSession getUserSession(@NotNull Player player) {
        return getUserSession(player.getUniqueId());
    }

    int getBlocksPerTick();

    void setBlocksPerTick(int blocks);

    HistoryManager getHistoryManager();
}
