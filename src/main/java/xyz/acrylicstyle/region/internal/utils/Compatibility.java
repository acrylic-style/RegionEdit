package xyz.acrylicstyle.region.internal.utils;

import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.PlayerInventory;
import util.ReflectionHelper;

public class Compatibility {
    public static boolean checkBlock_getData() {
        return ReflectionHelper.findMethod(Block.class, "getData") != null;
    }

    public static boolean checkPlayerInteractEvent_getHand() {
        return ReflectionHelper.findMethod(PlayerInteractEvent.class, "getHand") != null;
    }

    public static boolean checkPlayerInventory_getItemInHand() {
        return ReflectionHelper.findMethod(PlayerInventory.class, "getItemInHand") != null;
    }
}
