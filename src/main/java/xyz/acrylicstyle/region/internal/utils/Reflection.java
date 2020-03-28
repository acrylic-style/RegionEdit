package xyz.acrylicstyle.region.internal.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import util.ReflectionHelper;
import xyz.acrylicstyle.should.Should;

import java.lang.reflect.InvocationTargetException;

public class Reflection {
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
}
