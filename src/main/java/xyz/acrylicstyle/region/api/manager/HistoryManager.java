package xyz.acrylicstyle.region.api.manager;

import org.bukkit.Location;
import org.bukkit.block.Block;
import util.Collection;
import util.CollectionList;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class HistoryManager {
    public Collection<UUID, CollectionList<Collection<Location, Block>>> histories = new Collection<>();
    public Collection<UUID, AtomicInteger> indexes = new Collection<>();

    public void addEntry(UUID uuid, CollectionList<Block> blocks) {
        if (!histories.containsKey(uuid)) histories.add(uuid, new CollectionList<>());
        Collection<Location, Block> locationBlockCollection = new Collection<>();
        blocks.forEach(block -> locationBlockCollection.add(block.getLocation(), block));
        histories.get(uuid).add(locationBlockCollection);
    }

    public Collection<Location, Block> get(UUID uuid) {
        return histories.get(uuid).clone().reverse().get(indexes.getOrDefault(uuid, new AtomicInteger()).get());
    }

    public Collection<Location, Block> previous(UUID uuid) {
        if (!indexes.containsKey(uuid)) indexes.add(uuid, new AtomicInteger());
        if (indexes.get(uuid).get() <= 1) return null;
        indexes.get(uuid).decrementAndGet();
        return get(uuid);
    }

    public Collection<Location, Block> next(UUID uuid) {
        if (!indexes.containsKey(uuid)) indexes.add(uuid, new AtomicInteger());
        if (indexes.get(uuid).get() > histories.get(uuid).size()) return null;
        indexes.get(uuid).incrementAndGet();
        return get(uuid);
    }

    public void resetPointer(UUID uuid) {
        indexes.remove(uuid);
    }
}
