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
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.ReflectionHelper;
import xyz.acrylicstyle.craftbukkit.CraftUtils;
import xyz.acrylicstyle.craftbukkit.v1_8_R3.entity.CraftPlayer;
import xyz.acrylicstyle.minecraft.BlockPosition;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.block.BlockData;
import xyz.acrylicstyle.region.internal.nms.PacketPlayOutMapChunk;
import xyz.acrylicstyle.tomeito_core.utils.Log;
import xyz.acrylicstyle.tomeito_core.utils.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflection {
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
                return (ItemStack) ReflectionHelper.invokeMethod(player.getInventory().getClass(), player.getInventory(), "getItemInMainHand");
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
                return (EquipmentSlot) ReflectionHelper.invokeMethod(e.getClass(), e, "getHand");
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
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
    public static BlockData getBlockData(@NotNull Block block) {
        if (Compatibility.checkBlockData()) {
            try {
                return new BlockData(ReflectionHelper.invokeMethod(block.getClass(), block, "getBlockData"));
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void setBlockData(@NotNull BlockData blockData, boolean applyPhysics) {
        if (!Compatibility.checkBlockData()) return;
        try {
            ReflectionHelper.invokeMethod(blockData.getBukkitBlock().getClass(), blockData.getBukkitBlock(), "setBlockData", blockData.getData(), applyPhysics);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public static void sendBlockChange(@NotNull Player player, Location location, Material material, byte data, Object blockData) {
        if (Compatibility.checkOldPlayer_sendBlockChange()) {
            Validate.notNull(location, "Location cannot be null");
            Validate.notNull(material, "Material cannot be null");
            player.sendBlockChange(location, material, data);
        } else {
            try {
                Validate.notNull(blockData, "BlockData cannot be null");
                ReflectionHelper.invokeMethod(Player.class, player, "sendBlockChange", location, blockData);
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    public static void markDirty(Chunk chunk) {
        if (!Compatibility.checkChunk_markDirty()) return;
        try {
            CraftUtils.getHandle(chunk).getClass().getMethod("markDirty").invoke(CraftUtils.getHandle(chunk));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            Log.error("Couldn't invoke markDirty method:");
            e.printStackTrace();
        }
    }

    public static void notify(World bukkitWorld, Block block, BlockPosition blockPosition) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Compatibility.checkWorld_notify()) {
                    try {
                        ReflectionUtil
                                .getNMSClass("World")
                                .getMethod("notify", ReflectionUtil.getNMSClass("BlockPosition"))
                                .invoke(CraftUtils.getHandle(bukkitWorld), blockPosition.toNMSClass());
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
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
                        ReflectionUtil
                                .getNMSClass("World")
                                .getMethod("notify", ReflectionUtil.getNMSClass("BlockPosition"), ReflectionUtil.getNMSClass("IBlockData"), ReflectionUtil.getNMSClass("IBlockData"), int.class)
                                .invoke(CraftUtils.getHandle(bukkitWorld), blockPosition.toNMSClass(), iBlockData, iBlockData, 1);
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
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
            Object playerConnection = entityPlayer.getClass()
                    .getField("playerConnection")
                    .get(entityPlayer);
            playerConnection.getClass()
                    .getMethod("sendPacket", ReflectionUtil.getNMSClass("Packet"))
                    .invoke(playerConnection, new PacketPlayOutMapChunk(chunk).getNMSClass());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
