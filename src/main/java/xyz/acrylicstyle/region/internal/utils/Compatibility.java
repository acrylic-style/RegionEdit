package xyz.acrylicstyle.region.internal.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.PlayerInventory;
import util.ReflectionHelper;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

public final class Compatibility {
    public static BukkitVersion getBukkitVersion() {
        if (checkPacketPlayOutMapChunk1_16Constructor()) return BukkitVersion.v1_16;
        if (!checkBlock_getData()) return BukkitVersion.UNKNOWN;
        if (!checkOldPlayer_sendBlockChange()) return BukkitVersion.UNKNOWN; // There are no known versions of Bukkit API that doesn't have this method.
        if (!checkPlayerInteractEvent_getHand()) return BukkitVersion.v1_8; // returns false in 1.8
        if (!checkOldChunkSectionConstructor()) return BukkitVersion.v1_14;
        if (checkChunk_setType()) return BukkitVersion.v1_13_2;
        if (checkLightEngine()) return BukkitVersion.v1_13;
        if (!checkPlayerInventory_getItemInHand()) return BukkitVersion.v1_13; // returns false in 1.13+
        return BukkitVersion.v1_9; // otherwise
    }

    /**
     * Checks compatibility for nms.ChunkSection constructor (1.8 - 1.13.2).<br />
     * For 1.8 - 1.13.2, it returns true.<br />
     * For 1.14+, it returns false.
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
    public static boolean checkPacketPlayOutMapChunk1_8Constructor() {
        try {
            return ReflectionHelper.findConstructor(ReflectionUtil.getNMSClass("PacketPlayOutMapChunk"), ReflectionUtil.getNMSClass("Chunk"), boolean.class, int.class) != null;
        } catch (ClassNotFoundException ignored) {}
        return false;
    }

    /**
     * Checks compatibility for nms.PacketPlayOutMapChunk constructor (1.16).<br />
     * For 1.16+, it returns true.
     * Otherwise it returns false.
     */
    public static boolean checkPacketPlayOutMapChunk1_16Constructor() {
        try {
            return ReflectionHelper.findConstructor(ReflectionUtil.getNMSClass("PacketPlayOutMapChunk"), ReflectionUtil.getNMSClass("Chunk"), int.class, boolean.class) != null;
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
     * Returns true on all known versions (1.8-1.16.2).<br />
     * There are no known versions of Bukkit API that doesn't have this method.
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
     * For 1.8-1.16.2, it returns true.<br />
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

    /**
     * Checks compatibility for Chunk#setType.<br />
     * For 1.8 - 1.13.1, it returns false.<br />
     * For 1.13.2+, it returns true.
     */
    public static boolean checkChunk_setType() {
        try {
            return ReflectionHelper.findMethod(
                    ReflectionUtil.getNMSClass("Chunk"),
                    "setType",
                    ReflectionUtil.getNMSClass("BlockPosition"),
                    ReflectionUtil.getNMSClass("IBlockData"),
                    boolean.class
            ) != null;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks compatibility for LightEngine.<br />
     * For 1.8 - 1.12.2, it returns false.<br />
     * For 1.13+, it returns true.
     */
    public static boolean checkLightEngine() {
        try {
            ReflectionUtil.getNMSClass("LightEngine");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Checks compatibility for ChunkSection.<br />
     * @return always true?
     */
    public static boolean checkChunkSection() {
        try {
            ReflectionUtil.getNMSClass("ChunkSection");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Checks compatibility for Material#getMaterial(int).
     * For 1.8 - 1.12.2, it returns true.<br />
     * For 1.13+, it returns false.
     */
    public static boolean checkMaterial_getMaterial_I() {
        return ReflectionHelper.findMethod(Material.class, "getMaterial", int.class) != null;
    }

    /**
     * Checks if stationary_water exists in Material enum.<br />
     * For 1.8 - 1.12.2, it returns true.</br >
     * For 1.13+, it returns false.
     */
    public static boolean checkStationary_Water() {
        return Material.getMaterial("STATIONARY_WATER") != null;
    }

    public static Material getGoldenAxe() {
        return Material.getMaterial("GOLD_AXE") == null ? Material.getMaterial("GOLDEN_AXE") : Material.GOLD_AXE;
    }
}
