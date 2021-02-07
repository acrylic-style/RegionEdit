package xyz.acrylicstyle.region.api;

import net.querz.nbt.tag.CompoundTag;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Collection;
import util.CollectionList;
import util.CollectionSet;
import util.ICollectionList;
import xyz.acrylicstyle.region.api.block.state.BlockState;
import xyz.acrylicstyle.region.api.block.state.BlockStatePropertyMap;
import xyz.acrylicstyle.region.api.manager.HistoryManager;
import xyz.acrylicstyle.region.api.player.UserSession;
import xyz.acrylicstyle.region.api.schematic.Schematic;
import xyz.acrylicstyle.region.api.schematic.SchematicFormat;
import xyz.acrylicstyle.region.api.util.BlockPos;
import xyz.acrylicstyle.tomeito_api.utils.Callback;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * All methods common to RegionEdit operations.<br />
 * Static methods aren't required to load RegionEdit plugin.
 */
public interface RegionEdit extends Plugin {
    ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    static void drawParticleLine(@NotNull Player player, @NotNull Location _from, @NotNull Location _to) {
        /*
        Location from = _from.clone();
        Location to = _to.clone();
        if (!from.getWorld().equals(to.getWorld())) throw new IllegalArgumentException("Cannot draw particle line between 2 worlds");
        double distance = Math.min(_from.distance(to), 50);
        Vector direction = _from.clone().subtract(to).toVector();
        Log.info("From: " + from);
        for (double i = 0; i < distance; i += 0.1) {
            Location particle = _from.clone().add(direction.normalize().multiply(i));
            Log.info("P: " + particle);
            player.playEffect(particle, Effect.SNOW_SHOVEL, null);
        }
         */
    }

    /**
     * Obtain the RegionEdit instance.
     * @return RegionEdit instance
     */
    @NotNull
    static RegionEdit getInstance() {
        RegisteredServiceProvider<RegionEdit> service = Bukkit.getServicesManager().getRegistration(RegionEdit.class);
        if (service == null) throw new NoSuchElementException();
        return service.getProvider();
    }

    static void getBlocksAsync(@NotNull Location loc1, @NotNull Location loc2, @Nullable Material block, @Nullable Function<Block, Boolean> filterFunction, @NotNull Callback<CollectionList<Block>> callback) {
        pool.execute(() -> callback.done(getBlocks(loc1, loc2, block, filterFunction), null));
    }

    @NotNull
    static CollectionList<Block> getBlocks(@NotNull Location loc1, @NotNull Location loc2, @Nullable Material block, @Nullable Function<Block, Boolean> filterFunction) {
        CollectionList<Block> blocks = new CollectionList<>();
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
                        if (b.getType() == block) {
                            if (filterFunction == null) {
                                blocks.add(b);
                            } else {
                                if (filterFunction.apply(b)) blocks.add(b);
                            }
                        }
                    }
                }
            }
        }
        return blocks;
    }

    static void getBlocksInvertAsync(@NotNull Location loc1, @NotNull Location loc2, Material block, @NotNull Callback<CollectionList<Block>> callback) {
        pool.execute(() -> callback.done(getBlocksInvert(loc1, loc2, block), null));
    }

    @NotNull
    static CollectionList<Block> getBlocksInvert(@NotNull Location loc1, @NotNull Location loc2, Material block) {
        CollectionList<Block> blocks = new CollectionList<>();
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
        return blocks;
    }

    static void getNearbyBlocksAsync(@NotNull Location location, int radius, @NotNull Callback<ICollectionList<Block>> callback) {
        pool.execute(() -> callback.done(getNearbyBlocks(location, radius), null));
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
    static CollectionList<Block> getNearbyBlocks(@NotNull Location location, int radius, Predicate<Block> predicate) {
        CollectionList<Block> blocks = new CollectionList<>();
        for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    Block b = location.getWorld().getBlockAt(x, y, z);
                    if (predicate.test(b)) blocks.add(b);
                }
            }
        }
        return blocks;
    }

    static CollectionSet<Chunk> getChunks(Collection<BlockPos, BlockState> blocks) {
        CollectionSet<Chunk> chunks = new CollectionSet<>();
        blocks.forEach((l, b) -> chunks.add(l.getChunk()));
        return chunks;
    }

    static CollectionSet<Chunk> getChunks(CollectionList<Block> blocks) {
        CollectionSet<Chunk> chunks = new CollectionSet<>();
        blocks.forEach(b -> chunks.add(b.getChunk()));
        return chunks;
    }

    static CollectionSet<Chunk> getChunks(@NotNull World world, @NotNull CollectionList<BlockState> blocks) {
        CollectionSet<Chunk> chunks = new CollectionSet<>();
        blocks.forEach(b -> chunks.add(b.getBlockPos(world).getChunk()));
        new Thread(System::gc).start(); // run gc if we can
        return chunks;
    }

    void relightChunks(@NotNull ICollectionList<Chunk> chunks);

    static void unloadChunks(Collection<BlockPos, BlockState> blocks) {
        CollectionSet<Chunk> chunks = getChunks(blocks);
        new BukkitRunnable() {
            @Override
            public void run() {
                chunks.forEach(Chunk::unload);
            }
        }.runTask(getInstance());
    }

    static void unloadChunks(CollectionList<Block> blocks) {
        CollectionSet<Chunk> chunks = getChunks(blocks);
        new BukkitRunnable() {
            @Override
            public void run() {
                chunks.forEach(Chunk::unload);
            }
        }.runTask(getInstance());
    }

    static long memoryUsageInBytes() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    static double memoryUsageInGB() {
        return memoryUsageInBytes() / 1024D / 1024D / 1024D;
    }

    static double memoryUsageInGBRounded() {
        return Math.round(memoryUsageInGB() * 100) / 100D;
    }

    static void setBlocks(@NotNull World world, @NotNull CollectionList<BlockState> blocks) {
        pool.execute(() -> {
            AtomicInteger i = new AtomicInteger();
            blocks.forEach(block -> {
                try {
                    block.updateFast(world);
                } finally {
                    i.incrementAndGet();
                    if (i.get() >= blocks.size()) {
                        getInstance().relightChunks(getChunks(world, blocks));
                    }
                }
            });
        });
    }

    @NotNull
    Schematic loadSchematic(@NotNull SchematicFormat format, @NotNull CompoundTag tag);

    @NotNull
    Material getWandItem();

    @NotNull
    Material getNavigationItem();

    @Nullable
    Map.Entry<Material, Byte> resolveMaterial(@NotNull String id);

    @NotNull
    UserSession getUserSession(@NotNull UUID uuid);

    @NotNull
    default UserSession getUserSession(@NotNull Player player) {
        return getUserSession(player.getUniqueId());
    }

    int getBlocksPerTick();

    void setBlocksPerTick(int blocks);

    @NotNull
    HistoryManager getHistoryManager();

    @NotNull
    BlockState implementMethods(BlockState blockState);

    @NotNull
    BlockStatePropertyMap implementMethods(BlockStatePropertyMap propertyMap);

    void setBlocks(@Nullable Player player, @NotNull ICollectionList<Block> blocks, final Material material, final byte data);
}
