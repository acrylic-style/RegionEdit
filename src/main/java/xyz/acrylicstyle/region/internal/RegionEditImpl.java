package xyz.acrylicstyle.region.internal;

import net.querz.nbt.tag.CompoundTag;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Collection;
import util.CollectionList;
import util.ICollection;
import util.ICollectionList;
import util.javascript.JavaScript;
import util.reflector.Reflector;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.block.state.BlockState;
import xyz.acrylicstyle.region.api.block.state.BlockStatePropertyMap;
import xyz.acrylicstyle.region.api.block.state.EnumBlockPropertyKey;
import xyz.acrylicstyle.region.api.exception.RegionEditException;
import xyz.acrylicstyle.region.api.player.UserSession;
import xyz.acrylicstyle.region.api.region.RegionSelection;
import xyz.acrylicstyle.region.api.schematic.Schematic;
import xyz.acrylicstyle.region.api.schematic.SchematicFormat;
import xyz.acrylicstyle.region.api.selection.SelectionMode;
import xyz.acrylicstyle.region.api.util.NMSClasses;
import xyz.acrylicstyle.region.internal.block.BlockUtil;
import xyz.acrylicstyle.region.internal.command.CommandDescriptionManager;
import xyz.acrylicstyle.region.internal.manager.HistoryManagerImpl;
import xyz.acrylicstyle.region.internal.player.UserSessionImpl;
import xyz.acrylicstyle.region.api.reflector.net.minecraft.server.BlockStateList;
import xyz.acrylicstyle.region.api.reflector.net.minecraft.server.IBlockData;
import xyz.acrylicstyle.region.api.reflector.net.minecraft.server.IBlockState;
import xyz.acrylicstyle.region.api.reflector.org.bukkit.craftbukkit.block.CraftBlock;
import xyz.acrylicstyle.region.api.reflector.org.bukkit.craftbukkit.util.CraftMagicNumbers;
import xyz.acrylicstyle.region.internal.schematic.SchematicLegacy;
import xyz.acrylicstyle.region.internal.schematic.SchematicNew;
import xyz.acrylicstyle.region.internal.utils.BukkitVersion;
import xyz.acrylicstyle.region.internal.utils.Compatibility;
import xyz.acrylicstyle.region.internal.utils.Reflection;
import xyz.acrylicstyle.tomeito_api.utils.Log;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public abstract class RegionEditImpl extends JavaPlugin implements RegionEdit {
    protected static final HistoryManagerImpl historyManager = new HistoryManagerImpl();
    public static final BukkitVersion VERSION = Compatibility.getBukkitVersion();
    public static final Collection<UUID, SelectionMode> selectionMode = new Collection<>();
    public static final Collection<UUID, RegionSelection> regionSelection = new Collection<>();
    public static final CommandDescriptionManager commandDescriptionManager = new CommandDescriptionManager();
    public static final Collection<UUID, UserSession> sessions = new Collection<>();

    public static int blocksPerTick = 4096;

    protected Material selectionItem = null;
    protected Material navigationItem = null;

    @Override
    public void relightChunks(@NotNull ICollectionList<Chunk> chunks) {
        chunks.forEach(chunk -> xyz.acrylicstyle.region.internal.nms.Chunk.getInstance(chunk).initLighting());
    }

    @Override
    public @NotNull Schematic loadSchematic(@NotNull SchematicFormat format, @NotNull CompoundTag tag) {
        switch (format) {
            case LEGACY: return new SchematicLegacy(tag);
            case MODERN: return new SchematicNew(tag);
            default: throw new RuntimeException(format.name() + " has missing mapping!");
        }
    }

    @Override
    public @NotNull BlockState implementMethods(BlockState blockState) {
        return new BlockState(blockState) {
            @Override
            public void updateFast(@NotNull World world) {
                org.bukkit.Chunk chunk = world.getChunkAt(getLocation().getX() >> 4, getLocation().getZ() >> 4);
                if (!chunk.isLoaded()) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            chunk.load(true);
                            RegionEdit.pool.execute(() -> {
                                if (Compatibility.checkChunkSection()) {
                                    setBlockOld(world);
                                } else {
                                    setBlockNew(world);
                                }
                            });
                        }
                    }.runTask(RegionEditImpl.this);
                    return;
                }
                if (Compatibility.checkChunkSection()) {
                    setBlockOld(world);
                } else {
                    setBlockNew(world);
                }
            }

            @SuppressWarnings("deprecation")
            public void setBlockOld(World world) {
                int x = getLocation().getX();
                int y = getLocation().getY();
                int z = getLocation().getZ();
                IBlockData iBlockData;
                if (Compatibility.checkBlockData() && getPropertyMap() != null) {
                    iBlockData = getPropertyMap().getIBlockData(new MaterialData(type, data));
                } else {
                    iBlockData = xyz.acrylicstyle.region.api.reflector.net.minecraft.server.Block.STATIC.getByCombinedId(type.getId() + (data << 12));
                }
                //lastIBlockData = iBlockData;
                BlockUtil.setBlockOld(world, x, y, z, iBlockData);
            }

            @SuppressWarnings("deprecation")
            public void setBlockNew(World world) {
                int x = getLocation().getX();
                int y = getLocation().getY();
                int z = getLocation().getZ();
                org.bukkit.Chunk chunk = world.getChunkAt(x >> 4, z >> 4);
                Object iBlockData = null;
                if (getPropertyMap() != null) iBlockData = Reflector.getUnproxiedInstance(getPropertyMap().getIBlockData(new MaterialData(type, data))).get();
                xyz.acrylicstyle.region.internal.nms.Chunk.getInstance(chunk).setType(Reflection.newRawBlockPosition(x, y, z), iBlockData, false);
            }
        };
    }

    @Override
    public @NotNull BlockStatePropertyMap implementMethods(BlockStatePropertyMap propertyMap) {
        return new BlockStatePropertyMap(propertyMap) {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            public void apply(Block block) {
                if (Compatibility.getBukkitVersion() == BukkitVersion.v1_8) return;
                xyz.acrylicstyle.region.api.reflector.net.minecraft.server.Block nmsBlock = CraftBlock.getInstance(block).getNMSBlock();
                BlockStateList blockStateList = nmsBlock.getBlockStateList();
                if (blockStateList == null) return;
                AtomicReference<IBlockData> blockData = new AtomicReference<>(blockStateList.getBlockData());
                AtomicBoolean modified = new AtomicBoolean(false);
                forEach(entry -> {
                    EnumBlockPropertyKey prop = EnumBlockPropertyKey.getProperty(raw, entry.getKey());
                    if (prop == null) return;
                    IBlockState blockState = IBlockState.make(prop.getBlockProperty());
                    blockData.get().set(blockState, (Comparable) prop.getValueType().get(entry.getValue()));
                    modified.set(true);
                });
                if (modified.get()) nmsBlock.setBlockData(blockData.get()); // only update if BlockData is updated
                // this.j(this.blockStateList.getBlockData().set(BlockLeaves.DISTANCE, 7).set(BlockLeaves.PERSISTENT, false)); // for reference
            }

            private IBlockData cachedData;

            @SuppressWarnings({ "rawtypes", "unchecked" })
            public IBlockData getIBlockData(MaterialData data) {
                if (Compatibility.getBukkitVersion() == BukkitVersion.v1_8) return null;
                if (cachedData != null) return cachedData;
                Object blk;
                if (VERSION.atLeast(BukkitVersion.v1_13) && NMSClasses.Blocks() != null) {
                    blk = NMSClasses.Blocks().getField(data.getItemType().name()).get(null);
                } else {
                    blk = Reflection.getBlock(CraftMagicNumbers.INSTANCE.getBlock(data));
                }
                xyz.acrylicstyle.region.api.reflector.net.minecraft.server.Block nmsBlock = xyz.acrylicstyle.region.api.reflector.net.minecraft.server.Block.getInstance(blk);
                BlockStateList blockStateList = nmsBlock.getBlockStateList();
                if (blockStateList == null) return nmsBlock.getBlockData();
                AtomicReference<IBlockData> blockData = new AtomicReference<>(blockStateList.getBlockData());
                forEach(entry -> {
                    EnumBlockPropertyKey prop = EnumBlockPropertyKey.getProperty(raw, entry.getKey());
                    if (prop == null) return;
                    IBlockState blockState = IBlockState.make(prop.getBlockProperty());
                    try {
                        blockData.get().set(blockState, (Comparable) prop.getValueType().get(entry.getValue()));
                    } catch (IllegalArgumentException ex) {
                        throw new RegionEditException("Could not set block data for " + raw + " (tried to set " + entry.getKey() + "=" + entry.getValue() + ")", ex);
                    }
                });
                return cachedData = blockData.get();
            }
        };
    }

    @Override
    public @NotNull Material getWandItem() {
        return selectionItem;
    }

    @Override
    public @NotNull Material getNavigationItem() {
        return navigationItem;
    }

    @Override
    public @Nullable Map.Entry<Material, Byte> resolveMaterial(@NotNull String id) {
        if (id.matches("^\\d+|\\d+:\\d+$")) {
            String[] arr = id.split(":");
            int data;
            if (arr.length != 1) {
                data = Integer.parseInt(arr[1]);
            } else {
                data = -1;
            }
            return new AbstractMap.SimpleImmutableEntry<>(BlockUtil.getMaterialById(Integer.parseInt(arr[0])), (byte) data);
        } else {
            ICollectionList<String> materials = ICollectionList.asList(Material.values()).filter(Material::isBlock).map(Enum::name).map((Function<String, String>) String::toLowerCase);
            if (!materials.contains(id.split(":")[0].toLowerCase())) {
                return null;
            }
            int data = JavaScript.parseInt((id + ":-1").split(":")[1], 10);
            Log.debug("Data: " + data);
            return new AbstractMap.SimpleImmutableEntry<>(Material.getMaterial(Objects.requireNonNull(materials.filter(s -> s.equalsIgnoreCase((id + ":0").split(":")[0])).first()).toUpperCase()), (byte) data);
        }
    }

    @Override
    @NotNull
    public UserSession getUserSession(@NotNull final UUID uuid) {
        if (!sessions.containsKey(uuid)) sessions.add(uuid, new UserSessionImpl(this, uuid));
        return sessions.get(uuid);
    }

    @Override
    public int getBlocksPerTick() {
        return blocksPerTick;
    }

    @Override
    public void setBlocksPerTick(int blocks) {
        if (blocks <= 0) throw new RegionEditException("Blocks per ticks cannot be lower than 1");
        blocksPerTick = blocks;
    }

    @Override
    public @NotNull HistoryManagerImpl getHistoryManager() { return historyManager; }

    protected static <V> @NotNull ICollectionList<ICollectionList<V>> split(ICollectionList<V> collectionList, int max) {
        CollectionList<ICollectionList<V>> list = new CollectionList<>();
        collectionList.foreach((v, i) -> {
            if (i % max == 0) {
                list.add(new CollectionList<>());
            }
            Objects.requireNonNull(list.last()).add(v);
        });
        return list;
    }

    protected static <K, V> @NotNull ICollectionList<ICollection<K, V>> split(ICollection<K, V> collection, int max) {
        CollectionList<ICollection<K, V>> list = new CollectionList<>();
        collection.forEach((k, v, i, arr) -> {
            if (i % max == 0) {
                list.add(new Collection<>());
            }
            Objects.requireNonNull(list.last()).add(k, v);
        });
        return list;
    }
}
