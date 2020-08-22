package xyz.acrylicstyle.region.internal.listener;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.player.UserSession;
import xyz.acrylicstyle.tomeito_api.utils.Log;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class CUIChannelListener implements PluginMessageListener {
    public static final Charset CHARSET = StandardCharsets.UTF_8;

    public final RegionEdit plugin;

    public CUIChannelListener(RegionEditPlugin plugin) { this.plugin = plugin; }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        UserSession session = plugin.getUserSession(player);
        Log.as("RegionEdit").info("Enabling CUI support for " + player.getName() + " (Handshake received)");
        session.handleCUIInitialization();
        session.sendCUIEvent();
    }
}
