package xyz.acrylicstyle.region.internal.manager;

import org.bukkit.Location;
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
    public final Collection<UUID, CollectionList<Map.Entry<Collection<Location, Block>, Collection<Location, Block>>>> histories = new Collection<>();
    public final Collection<UUID, AtomicInteger> indexes = new Collection<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEntry(UUID uuid, CollectionList<Block> blocks) {
        if (!histories.containsKey(uuid)) histories.add(uuid, new CollectionList<>());
        Collection<Location, Block> locationBlockCollection = new Collection<>();
        blocks.forEach(block -> locationBlockCollection.add(block.getLocation(), block));
        Collection<Location, Block> undoBlocks = new Collection<>();
        blocks.forEach((b) -> undoBlocks.add(b.getLocation(), new RegionBlock(b.getLocation().getBlock())));
        histories.get(uuid).add(new AbstractMap.SimpleEntry<>(locationBlockCollection, undoBlocks));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Location, Block> get(UUID uuid) {
        return histories.get(uuid).clone().reverse().get(indexes.get(uuid).get()).getKey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Location, Block> getUndo(UUID uuid) {
        return histories.get(uuid).clone().reverse().get(indexes.get(uuid).get()).getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Location, Block> previous(UUID uuid) {
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
    public Collection<Location, Block> next(UUID uuid) {
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
    public void resetPointer(UUID uuid) {
        indexes.remove(uuid);
    }
}
