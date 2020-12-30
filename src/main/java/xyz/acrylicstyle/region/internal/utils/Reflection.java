package xyz.acrylicstyle.region.internal.utils;

import org.apache.commons.lang.Validate;
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
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.ReflectionHelper;
import util.reflect.Ref;
import xyz.acrylicstyle.craftbukkit.v1_8_R3.util.CraftUtils;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.internal.block.RegionBlockData;
import xyz.acrylicstyle.region.internal.nms.PacketPlayOutMapChunk;
import xyz.acrylicstyle.tomeito_api.utils.Log;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static xyz.acrylicstyle.region.api.util.NMSClasses.*;

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
            try {
                return new RegionBlockData(block, ReflectionHelper.invokeMethod(block.getClass(), block, "getBlockData"));
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
            }
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
        Object o = Ref.getClass(Material.class).getMethod("createBlockData").invoke(material);
        return new RegionBlockData(location.getBlock(), o);
    }

    @SuppressWarnings("deprecation")
    public static void setBlockData(@NotNull RegionBlockData blockData, boolean applyPhysics) {
        if (!Compatibility.checkBlockData()) return;
        Ref.getClass(blockData.getBukkitBlock().getClass())
                .getMethod("setTypeAndData", Ref.forName(ReflectionUtil.getNMSPackage() + ".IBlockData").getClazz(), boolean.class)
                .invokeObj(blockData.getBukkitBlock(), blockData.getState(), applyPhysics);
    }

    @SuppressWarnings({"deprecation", "JavaReflectionMemberAccess"})
    public static void sendBlockChange(@NotNull Player player, Location location, Material material, byte data, RegionBlockData blockData) {
        if (!Compatibility.checkNewPlayer_sendBlockChange()) {
            Validate.notNull(location, "Location cannot be null");
            Validate.notNull(material, "Material cannot be null");
            player.sendBlockChange(location, material, data);
        } else {
            try {
                Validate.notNull(blockData, "BlockData cannot be null");
                Player.class.getMethod("sendBlockChange", Location.class, Class.forName("org.bukkit.block.data.BlockData"))
                        .invoke(player, location, blockData.getHandle());
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e); // to avoid console spamming
            }
        }
    }

    @SuppressWarnings({"deprecation", "JavaReflectionMemberAccess"})
    public static void sendBlockChange(@NotNull Player player, Location location, Material material, byte data, Object iBlockData) {
        if (!Compatibility.checkNewPlayer_sendBlockChange()) {
            Validate.notNull(location, "Location cannot be null");
            Validate.notNull(material, "Material cannot be null");
            player.sendBlockChange(location, material, data);
        } else {
            try {
                Validate.notNull(iBlockData, "IBlockData cannot be null");
                Object blockData = ReflectionUtil.getNMSClass("BlockBase$BlockData").getMethod("createCraftBlockData").invoke(iBlockData);
                Player.class.getMethod("sendBlockChange", Location.class, Class.forName("org.bukkit.block.data.BlockData"))
                        .invoke(player, location, blockData);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e); // to avoid console spamming
            }
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

    public static void sendChunk(Player player, xyz.acrylicstyle.region.internal.nms.Chunk chunk) {
        try {
            Object entityPlayer = player.getClass()
                    .getMethod("getHandle")
                    .invoke(player);
            Object pc = playerConnection.get(entityPlayer);
            sendPacket.invoke(pc, new PacketPlayOutMapChunk(chunk).getNMSClass());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object newRawBlockPosition(int i0, int i1, int i2) {
        try {
            return BlockPosition.getConstructor(double.class, double.class, double.class)
                    .newInstance((double) i0, (double) i1, (double) i2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setBlockData(Block block, Object iBlockData) {
        if (Compatibility.getBukkitVersion() == BukkitVersion.v1_8) return;
        Object nmsBlock = Ref.forName(ReflectionUtil.getCraftBukkitPackage() + ".block.CraftBlock").getMethod("getNMS").invoke(block);
        Ref.forName(ReflectionUtil.getNMSPackage() + ".Block").getDeclaredField("blockData").accessible(true).set(nmsBlock, iBlockData);
    }

    public static Object getBlock(Material material, byte data) {
        return Ref.forName(ReflectionUtil.getCraftBukkitPackage() + ".util.CraftMagicNumbers")
                .getMethod("getBlock", Material.class, byte.class)
                .invoke(null, material, data);
    }

    public static Object getBlock(MaterialData material) {
        return Ref.forName(ReflectionUtil.getCraftBukkitPackage() + ".util.CraftMagicNumbers")
                .getMethod("getBlock", MaterialData.class)
                .invoke(null, material);
    }

    public static Object getBlock(Object iBlockData) {
        BukkitVersion version = Compatibility.getBukkitVersion();
        if (version.ordinal() < BukkitVersion.v1_16.ordinal()) {
            try {
                return IBlockData.getMethod("getBlock").invoke(iBlockData);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        } else {
            return Ref.forName(ReflectionUtil.getNMSPackage() + ".IBlockDataHolder").getDeclaredField("c").accessible(true).get(iBlockData);
        }
    }

    public static void hackChunkSection(Object chunkSection) {
        try {
            hackReentrantLock(Ref.forName(ReflectionUtil.getNMSPackage() + ".ChunkSection").getDeclaredField("blockIds").accessible(true).get(chunkSection));
        } catch (ReflectiveOperationException | RuntimeException ignore) {}
    }

    public static void hackReentrantLock(Object dataPaletteBlock) throws ReflectiveOperationException {
        Ref.forName(ReflectionUtil.getNMSPackage() + ".DataPaletteBlock")
                .getDeclaredField("j")
                .accessible(true)
                .removeFinal()
                .set(dataPaletteBlock, new NopeReentrantLock());
    }
}
