package xyz.acrylicstyle.region.internal.nms;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import util.reflect.Ref;
import xyz.acrylicstyle.craftbukkit.v1_8_R3.util.CraftUtils;
import xyz.acrylicstyle.region.api.util.NMSClasses;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

import java.lang.reflect.Method;
import java.util.Objects;

public class ActionBar {
    public static void sendActionbar(Player player, String message) {
        if (player == null || message == null) return;
        String nmsVersion = Bukkit.getServer().getClass().getPackage().getName();
        nmsVersion = nmsVersion.substring(nmsVersion.lastIndexOf(".") + 1);
        if (!nmsVersion.startsWith("v1_9_R") && !nmsVersion.startsWith("v1_8_R")) {
            Ref.getClass(Player.Spigot.class)
                    .getMethod("sendMessage", ChatMessageType.class, BaseComponent.class)
                    .invoke(player.spigot(), ChatMessageType.ACTION_BAR, new TextComponent(message));
            return;
        }
        try {
            Class<?> ppoc = ReflectionUtil.getNMSClass("PacketPlayOutChat");
            Class<?> chat = ReflectionUtil.getNMSClass((nmsVersion.equalsIgnoreCase("v1_8_R1") ? "ChatSerializer" : "ChatComponentText"));
            Class<?> chatBaseComponent = ReflectionUtil.getNMSClass("IChatBaseComponent");
            Method method = null;
            if (nmsVersion.equalsIgnoreCase("v1_8_R1")) method = chat.getDeclaredMethod("a", String.class);
            Object object = nmsVersion.equalsIgnoreCase("v1_8_R1") ? chatBaseComponent.cast(Objects.requireNonNull(method).invoke(chat, "{'text': '" + message + "'}")) : chat.getConstructor(String.class).newInstance(message);
            Object packetPlayOutChat = ppoc.getConstructor(chatBaseComponent, Byte.TYPE).newInstance(object, (byte) 2);
            Object playerConnection = NMSClasses.playerConnection.get(CraftUtils.getHandle(player));
            NMSClasses.sendPacket.invoke(playerConnection, packetPlayOutChat);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
