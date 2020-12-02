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
import util.ICollectionList;
import util.javascript.JavaScript;
import util.reflect.Ref;
import util.reflect.RefClass;
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
import xyz.acrylicstyle.region.internal.block.Blocks;
import xyz.acrylicstyle.region.internal.command.CommandDescriptionManager;
import xyz.acrylicstyle.region.internal.manager.HistoryManagerImpl;
import xyz.acrylicstyle.region.internal.player.UserSessionImpl;
import xyz.acrylicstyle.region.internal.schematic.SchematicLegacy;
import xyz.acrylicstyle.region.internal.schematic.SchematicNew;
import xyz.acrylicstyle.region.internal.utils.BukkitVersion;
import xyz.acrylicstyle.region.internal.utils.Compatibility;
import xyz.acrylicstyle.region.internal.utils.Reflection;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class RegionEditImpl extends JavaPlugin implements RegionEdit {
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
        chunks.forEach(chunk -> xyz.acrylicstyle.region.internal.nms.Chunk.wrap(chunk).initLighting());
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
                Object blockId;
                if (Compatibility.checkBlockData() && getPropertyMap() != null) {
                    blockId = getPropertyMap().getIBlockData(new MaterialData(type, data));
                } else {
                    blockId = Blocks.getByCombinedId(type.getId() + (data << 12));
                }
                //lastIBlockData = blockId;
                xyz.acrylicstyle.region.internal.nms.Chunk.wrap(world.getChunkAt(x >> 4, z >> 4)).sections[y >> 4].setType(x & 15, y & 15, z & 15, blockId);
            }

            @SuppressWarnings("deprecation")
            public void setBlockNew(World world) {
                int x = getLocation().getX();
                int y = getLocation().getY();
                int z = getLocation().getZ();
                Block block = world.getBlockAt(x, y, z);
                org.bukkit.Chunk chunk = block.getChunk();
                xyz.acrylicstyle.region.internal.nms.Chunk.wrap(chunk).setType(Reflection.newRawBlockPosition(x, y, z), getPropertyMap() == null ? null : getPropertyMap().getIBlockData(new MaterialData(type, data)), false);
            }
        };
    }

    @Override
    public @NotNull BlockStatePropertyMap implementMethods(BlockStatePropertyMap propertyMap) {
        return new BlockStatePropertyMap(propertyMap) {
            @SuppressWarnings("unchecked")
            public void apply(Block block) {
                if (Compatibility.getBukkitVersion() == BukkitVersion.v1_8) return;
                Object nmsBlock = Ref.forName(ReflectionUtil.getCraftBukkitPackage() + ".block.CraftBlock").getMethod("getNMS").invoke(block);
                Object blockStateList = new RefClass<>((Class<Object>) NMSClasses.Block).getDeclaredField("blockStateList").accessible(true).get(nmsBlock);
                try {
                    AtomicReference<Object> blockData = new AtomicReference<>(blockStateList.getClass().getMethod("getBlockData").invoke(blockStateList));
                    Method method = ReflectionUtil.getNMSClass("IBlockDataHolder").getMethod("set", ReflectionUtil.getNMSClass("IBlockState"), Object.class);
                    AtomicBoolean modified = new AtomicBoolean(false);
                    forEach(entry -> {
                        EnumBlockPropertyKey prop = EnumBlockPropertyKey.getProperty(raw, entry.getKey());
                        try {
                            if (prop != null) {
                                blockData.set(method.invoke(blockData.get(), prop.getBlockProperty(), prop.getValueType().get(entry.getValue())));
                                modified.set(true);
                            }
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    });
                    if (modified.get()) Reflection.setBlockData(block, blockData); // only update if BlockData is updated
                } catch (ReflectiveOperationException ignore) {} // some mc versions doesn't have this feature
                // this.j(this.blockStateList.getBlockData().set(BlockLeaves.DISTANCE, 7).set(BlockLeaves.PERSISTENT, false)); // for reference
            }

            private Object cachedData;

            @SuppressWarnings("unchecked")
            public Object getIBlockData(MaterialData data) {
                if (Compatibility.getBukkitVersion() == BukkitVersion.v1_8) return null;
                if (cachedData != null) return cachedData;
                Object blk;
                if (VERSION.atLeast(BukkitVersion.v1_13) && NMSClasses.Blocks() != null) {
                    blk = NMSClasses.Blocks().getField(data.getItemType().name()).get(null);
                } else {
                    blk = Reflection.getBlock(Reflection.getBlock(data));
                }
                Object blockStateList = new RefClass<>((Class<Object>) NMSClasses.Block).getDeclaredField("blockStateList").accessible(true).get(blk);
                try {
                    AtomicReference<Object> blockData = new AtomicReference<>(blockStateList.getClass().getMethod("getBlockData").invoke(blockStateList));
                    Method method = ReflectionUtil.getNMSClass("IBlockDataHolder").getMethod("set", ReflectionUtil.getNMSClass("IBlockState"), Comparable.class);
                    forEach(entry -> {
                        EnumBlockPropertyKey prop = EnumBlockPropertyKey.getProperty(raw, entry.getKey());
                        try {
                            if (prop != null) {
                                try {
                                    blockData.set(method.invoke(blockData.get(), prop.getBlockProperty(), prop.getValueType().get(entry.getValue())));
                                } catch (IllegalArgumentException ex) {
                                    throw new RegionEditException("Could not set block data for " + raw + " (tried to set " + entry.getKey() + "=" + entry.getValue() + ")", ex);
                                }
                            }
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            if (e.getCause() instanceof IllegalArgumentException) return; // ignore
                            e.printStackTrace();
                        }
                    });
                    cachedData = blockData.get();
                    return blockData.get();
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
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
                data = 0;
            }
            return new AbstractMap.SimpleImmutableEntry<>(Blocks.getMaterialById(Integer.parseInt(arr[0])), (byte) data);
        } else {
            CollectionList<String> materials = ICollectionList.asList(Material.values()).filter(Material::isBlock).map(Enum::name).map((Function<String, String>) String::toLowerCase);
            if (!materials.contains(id.split(":")[0].toLowerCase())) {
                return null;
            }
            int data = JavaScript.parseInt((id + ":0").split(":")[1], 10);
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
}
