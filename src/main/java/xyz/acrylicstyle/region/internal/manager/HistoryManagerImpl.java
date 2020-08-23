package xyz.acrylicstyle.region.internal.manager;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import util.Collection;
import util.CollectionList;
import xyz.acrylicstyle.region.api.block.Block;
import xyz.acrylicstyle.region.api.manager.HistoryManager;
import xyz.acrylicstyle.region.internal.block.RegionBlock;

import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class HistoryManagerImpl implements HistoryManager {
    @NotNull
    public final Collection<UUID, CollectionList<Map.Entry<Collection<Location, Block>, Collection<Location, Block>>>> histories = new Collection<>();

    @NotNull
    public final Collection<UUID, AtomicInteger> indexes = new Collection<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEntry(@NotNull UUID uuid, @NotNull CollectionList<Block> blocks) {
        resetPointer(uuid);
        if (!histories.containsKey(uuid)) histories.add(uuid, new CollectionList<>());
        Collection<Location, Block> locationBlockCollection = new Collection<>();
        blocks.forEach(block -> locationBlockCollection.add(block.getLocation(), block));
        Collection<Location, Block> undoBlocks = new Collection<>();
        blocks.forEach(b -> undoBlocks.add(b.getLocation(), new RegionBlock(b.getLocation().getBlock())));
        histories.get(uuid).add(new AbstractMap.SimpleEntry<>(locationBlockCollection, undoBlocks));
    }

    @Override
    public void addEntry(@NotNull UUID uuid, @NotNull Collection<Location, Block> blocks) {
        resetPointer(uuid);
        if (!histories.containsKey(uuid)) histories.add(uuid, new CollectionList<>());
        Collection<Location, Block> undoBlocks = new Collection<>();
        blocks.forEach((l, b) -> undoBlocks.add(l, new RegionBlock(l.getBlock())));
        histories.get(uuid).add(new AbstractMap.SimpleEntry<>(undoBlocks, blocks));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Location, Block> get(@NotNull UUID uuid) {
        return histories.get(uuid).clone().reverse().get(indexes.get(uuid).get()).getKey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Location, Block> getUndo(@NotNull UUID uuid) {
        return histories.get(uuid).clone().reverse().get(indexes.get(uuid).get()).getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Location, Block> previous(@NotNull UUID uuid) {
        if (!indexes.containsKey(uuid)) indexes.add(uuid, new AtomicInteger());
        if (indexes.get(uuid).get()-1 < 0) return null;
        indexes.get(uuid).decrementAndGet();
        Collection<Location, Block> blocks = getUndo(uuid);
        indexes.get(uuid).incrementAndGet();
        if (indexes.get(uuid).get()-1 >= 0) indexes.get(uuid).getAndDecrement(); // decrease only if it makes sense
        return blocks;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Location, Block> next(@NotNull UUID uuid) {
        if (!indexes.containsKey(uuid)) indexes.add(uuid, new AtomicInteger());
        if (indexes.get(uuid).get()+1 > histories.get(uuid).size()) return null;
        Collection<Location, Block> blocks = get(uuid);
        if (indexes.get(uuid).get()+1 <= histories.get(uuid).size()) indexes.get(uuid).getAndIncrement(); // increase only if it makes sense
        return blocks;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetPointer(@NotNull UUID uuid) {
        indexes.remove(uuid);
    }
}
