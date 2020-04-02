package xyz.acrylicstyle.region.internal.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.PlayerInventory;
import util.ReflectionHelper;
import xyz.acrylicstyle.tomeito_core.utils.ReflectionUtil;

public class Compatibility {
    public static BukkitVersion getBukkitVersion() {
        if (!checkOldPlayer_sendBlockChange()) return BukkitVersion.UNKNOWN; // There are no known versions of Bukkit API that doesn't have this method.
        if (!checkPlayerInteractEvent_getHand()) return BukkitVersion.v1_8; // returns false in 1.8
        if (!checkPlayerInventory_getItemInHand()) return BukkitVersion.v1_13; // returns false in 1.13+
        return BukkitVersion.v1_9; // otherwise
    }

    /**
     * Checks compatibility for nms.ChunkSection constructor (1.8 - 1.12.2).<br />
     * For 1.8 - 1.12.2, it returns true.<br />
     * For 1.13+, it returns false.
     */
    public static boolean checkOldChunkSectionConstructor() {
        try {
            return ReflectionHelper.findConstructor(ReflectionUtil.getNMSClass("ChunkSection"), int.class, boolean.class) != null;
        } catch (ClassNotFoundException ignored) {}
        return false;
    }

    /**
     * Checks compatibility for nms.PacketPlayOutMapChunk constructor (1.8).<br />
     * For 1.8, it returns true.<br />
     * For 1.9+, it returns false.
     */
    public static boolean checkPacketPlayOutMapChunkOldConstructor() {
        try {
            return ReflectionHelper.findConstructor(ReflectionUtil.getNMSClass("PacketPlayOutMapChunk"), ReflectionUtil.getNMSClass("Chunk"), boolean.class, int.class) != null;
        } catch (ClassNotFoundException ignored) {}
        return false;
    }

    /**
     * Checks compatibility for nms.Chunk#markDirty().<br />
     * For 1.8, it returns false.<br />
     * For 1.9+, it returns true.
     */
    public static boolean checkChunk_markDirty() {
        try {
            return ReflectionHelper.findMethod(ReflectionUtil.getNMSClass("Chunk"), "markDirty") != null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks compatibility for nms.World#notify(BlockPosition).<br />
     * For 1.8, it returns true.<br />
     * For 1.9+, it returns false.
     */
    public static boolean checkWorld_notify() {
        try {
            return ReflectionHelper.findMethod(ReflectionUtil.getNMSClass("World"), "notify", ReflectionUtil.getNMSClass("BlockPosition")) != null;
        } catch (ClassNotFoundException ignored) {}
        return false;
    }

    /**
     * Checks compatibility for {@link Block#getData()}.<br />
     * For 1.8-1.12.2, it returns true.<br />
     * For 1.13+, it returns true.
     */
    @SuppressWarnings("deprecation")
    public static boolean checkBlock_getData() {
        return ReflectionHelper.findMethod(Block.class, "getData") != null;
    }

    /**
     * Checks compatibility for PlayerInteractEvent#getHand().<br />
     * For 1.8, it returns false.<br />
     * For 1.9+, it returns true.
     */
    public static boolean checkPlayerInteractEvent_getHand() {
        return ReflectionHelper.findMethod(PlayerInteractEvent.class, "getHand") != null;
    }

    /**
     * Checks compatibility for {@link PlayerInventory#getItemInHand()}.<br />
     * For 1.8 - 1.12.2, it return true.<br />
     * For 1.13+, it returns false.
     */
    public static boolean checkPlayerInventory_getItemInHand() {
        return ReflectionHelper.findMethod(PlayerInventory.class, "getItemInHand") != null;
    }

    /**
     * Checks compatibility for BlockData.<br />
     * For 1.8 - 1.12.2, it returns false.<br />
     * For 1.13+, it returns true.
     */
    public static boolean checkBlockData() {
        try {
            Class.forName("org.bukkit.block.data.BlockData");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Checks compatibility for {@link Player#sendBlockChange(Location, int, byte)}.<br />
     * For 1.8+, it returns true.<br />
     * There are no known versions of Bukkit API that doesn't have this method.
     */
    @SuppressWarnings("deprecation")
    public static boolean checkOldPlayer_sendBlockChange() {
        return ReflectionHelper.findMethod(Player.class, "sendBlockChange", Location.class, Material.class, byte.class) != null;
    }

    /**
     * Checks compatibility for new sendBlockChange.<br />
     * For 1.8 - 1.12.2, it returns false.<br />
     * For 1.13+, it returns true.
     */
    public static boolean checkNewPlayer_sendBlockChange() {
        try {
            return ReflectionHelper.findMethod(Player.class, "sendBlockChange", Location.class, Class.forName("org.bukkit.block.data.BlockData")) != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
