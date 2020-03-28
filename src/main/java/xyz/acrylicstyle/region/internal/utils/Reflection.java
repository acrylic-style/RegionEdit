package xyz.acrylicstyle.region.internal.utils;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.ReflectionHelper;
import xyz.acrylicstyle.should.Should;

import java.lang.reflect.InvocationTargetException;

public class Reflection {
    /**
     * Returns get ItemStack in hand.<br />
     * For 1.8-1.12.2, it uses Inventory#getItemInHand.<br />
     * For 1.13+, it uses Inventory#getItemInMainHand.
     */
    public static ItemStack getItemInHand(@NotNull Player player) {
        if (Should.object(player.getInventory()).should.have().method("getItemInHand")) {
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
        if (Should.object(e).have().method("getHand")) {
            try {
                return (EquipmentSlot) ReflectionHelper.invokeMethod(e.getClass(), e, "getHand");
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        } else return null;
    }
}
