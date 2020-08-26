package xyz.acrylicstyle.region.internal.manager;

import org.jetbrains.annotations.NotNull;
import util.Collection;
import util.CollectionList;
import util.ICollectionList;
import util.file.FileBasedCollectionList;
import xyz.acrylicstyle.region.api.block.Block;
import xyz.acrylicstyle.region.api.block.state.BlockState;
import xyz.acrylicstyle.region.api.manager.HistoryManager;
import xyz.acrylicstyle.region.api.util.BlockPos;

import java.util.AbstractMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class HistoryManagerImpl implements HistoryManager {
    @NotNull
    public final Collection<UUID, ICollectionList<AbstractMap.SimpleEntry<Collection<BlockPos, BlockState>, Collection<BlockPos, BlockState>>>> histories = new Collection<>();

    @NotNull
    public final Collection<UUID, AtomicInteger> indexes = new Collection<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEntry(@NotNull UUID uuid, @NotNull ICollectionList<Block> blocks) {
        resetPointer(uuid);
        if (!histories.containsKey(uuid)) histories.add(uuid, new FileBasedCollectionList<>());
        Collection<BlockPos, BlockState> locationBlockCollection = new Collection<>();
        blocks.forEach(block -> locationBlockCollection.add(new BlockPos(block.getLocation()), new BlockState(block)));
        Collection<BlockPos, BlockState> undoBlocks = new Collection<>();
        blocks.forEach(b -> undoBlocks.add(new BlockPos(b.getLocation()), new BlockState(b.getLocation().getBlock())));
        histories.get(uuid).add(new AbstractMap.SimpleEntry<>(locationBlockCollection, undoBlocks));
    }

    @Override
    public void addEntry(@NotNull UUID uuid, @NotNull Collection<BlockPos, BlockState> blocks) {
        resetPointer(uuid);
        if (!histories.containsKey(uuid)) histories.add(uuid, new CollectionList<>());
        Collection<BlockPos, BlockState> undoBlocks = new Collection<>();
        blocks.forEach((l, b) -> undoBlocks.add(l, new BlockState(l.getBlock()))); // its important to get current state of blocks
        histories.get(uuid).add(new AbstractMap.SimpleEntry<>(undoBlocks, blocks));
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public Collection<BlockPos, BlockState> get(@NotNull UUID uuid) {
        return histories.get(uuid).clone().reverse().get(indexes.get(uuid).get()).getKey();
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public Collection<BlockPos, BlockState> getUndo(@NotNull UUID uuid) {
        return histories.get(uuid).clone().reverse().get(indexes.get(uuid).get()).getValue();
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public Collection<BlockPos, BlockState> previous(@NotNull UUID uuid) {
        if (!indexes.containsKey(uuid)) indexes.add(uuid, new AtomicInteger());
        if (indexes.get(uuid).get()-1 < 0) return null;
        indexes.get(uuid).decrementAndGet();
        Collection<BlockPos, BlockState> blocks = getUndo(uuid);
        indexes.get(uuid).incrementAndGet();
        if (indexes.get(uuid).get()-1 >= 0) indexes.get(uuid).getAndDecrement(); // decrease only if it makes sense
        return blocks;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public Collection<BlockPos, BlockState> next(@NotNull UUID uuid) {
        if (!indexes.containsKey(uuid)) indexes.add(uuid, new AtomicInteger());
        if (indexes.get(uuid).get()+1 > histories.get(uuid).size()) return null;
        Collection<BlockPos, BlockState> blocks = get(uuid);
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
