package xyz.acrylicstyle.region.api;

import util.reflect.Ref;

public class AsyncCatcher {
    public static void setEnabled(boolean enabled) {
        Ref.forName("org.spigotmc.AsyncCatcher").getField("enabled").set(null, enabled);
    }
}
