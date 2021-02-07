package xyz.acrylicstyle.region.internal.utils;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.ReflectionHelper;
import util.reflect.Ref;
import xyz.acrylicstyle.craftbukkit.v1_8_R3.util.CraftUtils;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.reflector.net.minecraft.server.ChunkSection;
import xyz.acrylicstyle.region.api.reflector.net.minecraft.server.IBlockData;
import xyz.acrylicstyle.region.api.reflector.net.minecraft.server.PacketPlayOutMultiBlockChange;
import xyz.acrylicstyle.region.api.reflector.net.minecraft.server.v1_13.IBlockDataHolder;
import xyz.acrylicstyle.region.api.reflector.net.minecraft.server.v1_16.SectionPosition;
import xyz.acrylicstyle.region.api.reflector.org.bukkit.craftbukkit.block.CraftBlock;
import xyz.acrylicstyle.region.api.reflector.org.bukkit.craftbukkit.v1_13.block.data.CraftBlockData;
import xyz.acrylicstyle.region.api.reflector.org.bukkit.entity.BukkitPlayer;
import xyz.acrylicstyle.region.api.util.BlockPos;
import xyz.acrylicstyle.region.internal.block.RegionBlockData;
import xyz.acrylicstyle.tomeito_api.utils.Log;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static xyz.acrylicstyle.region.api.util.NMSClasses.BlockPosition;
import static xyz.acrylicstyle.region.api.util.NMSClasses.Chunk;
import static xyz.acrylicstyle.region.api.util.NMSClasses.IBlockData;
import static xyz.acrylicstyle.region.api.util.NMSClasses.World;
import static xyz.acrylicstyle.region.api.util.NMSClasses.sendPacket;

public final class Reflection {
    /**
     * Returns get ItemStack in hand.<br />
     * For 1.8-1.12.2, it uses Inventory#getItemInHand.<br />
     * For 1.13+, it uses Inventory#getItemInMainHand.
     */
    @NotNull
    public static ItemStack getItemInHand(@NotNull Player player) {
        if (Compatibility.checkPlayerInventory_getItemInHand()) {
            return player.getInventory().getItemInHand();
        } else {
            try {
                return (ItemStack) ReflectionHelper.invokeMethod(PlayerInventory.class, player.getInventory(), "getItemInMainHand");
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Returns EquipmentSlot.<br />
     * For 1.8, it returns null since it's not supported.<br />
     * For 1.9+, it returns EquipmentSlot.
     */
    @Nullable
    public static EquipmentSlot getHand(PlayerInteractEvent e) {
        if (Compatibility.checkPlayerInteractEvent_getHand()) {
            try {
                return (EquipmentSlot) ReflectionHelper.invokeMethod(PlayerInteractEvent.class, e, "getHand");
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException ex) {
                return null;
            }
        } else return null;
    }

    /**
     * Returns block data.<br />
     * For 1.8-1.12.2, it returns actual block data value.<br />
     * For 1.13+, it always returns 0 since it's not supported.
     */
    @SuppressWarnings("deprecation")
    public static byte getData(@NotNull Block block) {
        if (Compatibility.checkBlock_getData()) return block.getData();
        return (byte) 0;
    }

    /**
     * Returns block data.<br />
     * For 1.8-1.12.2, it returns null.<br />
     * For 1.13, it returns block data.
     * @param block Block
     * @return A block data
     */
    @Nullable
    public static RegionBlockData getBlockData(@NotNull Block block) {
        if (Compatibility.checkBlockData()) {
            return new RegionBlockData(block, Objects.requireNonNull(CraftBlock.getInstance(block).getBlockData()));
        }
        return null;
    }

    /**
     * Returns block data by material.<br />
     * For 1.8-1.12.2, it returns null.<br />
     * For 1.13+, it returns block data.
     */
    @Nullable
    public static RegionBlockData createBlockData(@NotNull Location location, @NotNull Material material) {
        if (!Compatibility.checkBlockData()) return null;
        CraftBlockData blockData = xyz.acrylicstyle.region.api.reflector.org.bukkit.craftbukkit.v1_13.Material.getInstance(material).createBlockData();
        return new RegionBlockData(location.getBlock(), Objects.requireNonNull(blockData));
    }

    @SuppressWarnings("deprecation")
    public static void setBlockData(@NotNull RegionBlockData blockData, boolean applyPhysics) {
        if (!Compatibility.checkBlockData()) return;
        Ref.getClass(blockData.getBukkitBlock().getClass())
                .getMethod("setTypeAndData", Ref.forName(ReflectionUtil.getNMSPackage() + ".IBlockData").getClazz(), boolean.class)
                .invokeObj(blockData.getBukkitBlock(), blockData.getState(), applyPhysics);
    }

    @SuppressWarnings({"deprecation"})
    public static void sendBlockChange(@NotNull Player player, Location location, Material material, byte data, RegionBlockData blockData) {
        if (!Compatibility.checkNewPlayer_sendBlockChange()) {
            Validate.notNull(location, "Location cannot be null");
            Validate.notNull(material, "Material cannot be null");
            player.sendBlockChange(location, material, data);
        } else {
            Validate.notNull(blockData, "BlockData cannot be null");
            BukkitPlayer.getInstance(player).sendBlockChange(location, blockData.getHandle());
        }
    }

    @SuppressWarnings({"deprecation"})
    public static void sendBlockChange(@NotNull Player player, Location location, Material material, byte data, IBlockData iBlockData) {
        if (!Compatibility.checkNewPlayer_sendBlockChange()) {
            Validate.notNull(location, "Location cannot be null");
            Validate.notNull(material, "Material cannot be null");
            player.sendBlockChange(location, material, data);
        } else {
            Validate.notNull(iBlockData, "IBlockData cannot be null");
            BukkitPlayer.getInstance(player).sendBlockChange(location, iBlockData.createCraftBlockData());
        }
    }

    public static void markDirty(Chunk chunk) {
        if (!Compatibility.checkChunk_markDirty()) return;
        try {
            Chunk.getMethod("markDirty").invoke(CraftUtils.getHandle(chunk));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            Log.error("Couldn't invoke markDirty method:");
            e.printStackTrace();
        }
    }

    public static void notify(World bukkitWorld, Block block, Object blockPosition) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Compatibility.checkWorld_notify()) {
                    try {
                        World.getMethod("notify", BlockPosition)
                                .invoke(CraftUtils.getHandle(bukkitWorld), blockPosition);
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Method method;
                        try {
                            method = block.getClass().getDeclaredMethod("getData0");
                        } catch (NoSuchMethodException ignored) {
                            method = block.getClass().getMethod("getNMS");
                        }
                        method.setAccessible(true);
                        Object iBlockData = method.invoke(block);
                        World.getMethod("notify", BlockPosition, IBlockData, IBlockData, int.class)
                                .invoke(CraftUtils.getHandle(bukkitWorld), blockPosition, iBlockData, iBlockData, 1);
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        Log.error("Couldn't find method/class, or error occurred inside the method:");
                        e.printStackTrace();
                    }
                }
            }
        }.runTask(RegionEdit.getInstance());
    }

    public static Object newRawBlockPosition(int i0, int i1, int i2) {
        try {
            return BlockPosition.getConstructor(double.class, double.class, double.class)
                    .newInstance((double) i0, (double) i1, (double) i2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getBlock(IBlockData iBlockData) {
        BukkitVersion version = Compatibility.getBukkitVersion();
        if (version.ordinal() < BukkitVersion.v1_16.ordinal()) {
            return iBlockData.getBlock();
        } else {
            return Objects.requireNonNull(IBlockDataHolder.getInstance(iBlockData)).getBlock();
        }
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public static Set<Short> createShortSet() {
        try {
            return (Set<Short>) Class.forName("org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.shorts.ShortOpenHashSet").newInstance(); // spigot, craftbukkit
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            try {
                return (Set<Short>) Class.forName("it.unimi.dsi.fastutil.shorts.ShortOpenHashSet").newInstance(); // paper, just why.
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex1) {
                return new HashSet<>();
            }
        }
    }

    public static void sendBlockChangesWithLocations(Collection<? extends Player> players, Collection<Location> locations, xyz.acrylicstyle.region.internal.nms.Chunk chunk) {
        sendBlockChanges(players, locations.stream().map(BlockPos::new).collect(Collectors.toSet()), chunk);
    }

    public static void sendBlockChanges(Collection<? extends Player> players, Set<BlockPos> blockPos, xyz.acrylicstyle.region.internal.nms.Chunk chunk) {
        Set<Short> shortSet = createShortSet();
        blockPos.forEach(bp -> shortSet.add(xyz.acrylicstyle.region.api.block.Block.locationToShort(bp.getBlockX(), bp.getBlockY(), bp.getBlockZ())));
        Set<Object> packets = new HashSet<>();
        if (Compatibility.checkPacketPlayOutMultiBlockChange1_8Constructor()) {
            packets.add(PacketPlayOutMultiBlockChange.getInstance(null).constructor_v18_v1161(shortSet.size(), toShortArray(shortSet), xyz.acrylicstyle.region.api.reflector.net.minecraft.server.Chunk.getInstance(chunk.getHandle())).getHandle());
        } else {
            for (int i = 0; i < 15; i++) {
                PacketPlayOutMultiBlockChange packetPlayOutMultiBlockChange = PacketPlayOutMultiBlockChange.getInstance(null);
                packets.add(packetPlayOutMultiBlockChange.constructor_v1162(SectionPosition.getInstance(null).create(chunk.x, i, chunk.z), shortSet, ChunkSection.getInstance(chunk.sections[i]), false).getHandle());
            }
        }
        if (packets.size() > 0) {
            players.forEach(player -> packets.forEach(packet -> {
                try {
                    sendPacket.invoke(getPlayerConnection(player), packet);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }));
        }
    }

    public static Object getPlayerConnection(Player player) {
        try {
            Object ep = CraftUtils.getHandle(player);
            return ep.getClass().getDeclaredField("playerConnection").get(ep);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static short[] toShortArray(Set<Short> shorts) {
        short[] shortArray = new short[shorts.size()];
        int i = 0;
        for (Short s : shorts) {
            shortArray[i++] = s;
        }
        return shortArray;
    }
}
