package xyz.acrylicstyle.region.internal.schematic;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.tomeito_api.utils.Log;

import java.util.concurrent.atomic.AtomicInteger;

class SchematicUtil {
    static boolean checkConditions(int maxWidth, int maxHeight, int maxLength, @NotNull AtomicInteger width, @NotNull AtomicInteger height, @NotNull AtomicInteger length, boolean warnLogged) {
        if (width.get() > maxWidth) { // todo: make sure it's doing correctly
            width.set(0);
            length.incrementAndGet();
        }
        if (length.get() > maxLength) {
            length.set(0);
            height.incrementAndGet();
        }
        if (height.get() > maxHeight) {
            if (!warnLogged) {
                Log.warn("Current height is higher than maximum value! (curr: " + height.get() + ", max: " + maxHeight + ")");
                Log.warn("Pasting the schematic may produces the corrupted blocks, or the blocks may be placed on the wrong position.");
                Log.warn("Make sure the schematic isn't corrupted, then try again.");
                Log.warn("If you're using FastAsyncWorldEdit or AsyncWorldEdit and not sure if the schematic is corrupted or not, try using WorldEdit instead of (Fast)AsyncWorldEdit.");
                Log.warn("If it happens even if the schematic isn't corrupted, please report this to github with the Schematic Details.");
                warnLogged = true;
            }
            //height.set(0);
        }
        return warnLogged;
    }
}
