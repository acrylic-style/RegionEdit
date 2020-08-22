package xyz.acrylicstyle.region.api.block.state;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.CollectionList;
import util.ICollectionList;
import util.StringCollection;
import util.Validate;

import java.util.Map;
import java.util.function.BiConsumer;

public class BlockStatePropertyMap {
    private final StringCollection<String> nodes;

    public BlockStatePropertyMap() { nodes = new StringCollection<>(); }

    public BlockStatePropertyMap(@NotNull Map<String, String> map) { nodes = new StringCollection<>(map); }

    protected void add(@NotNull String key, @NotNull String value) { nodes.add(key, value); }

    public <T> T get(@NotNull String key, @NotNull BlockPropertyType<T> type) {
        String v = nodes.get(key);
        Validate.notNull(v, key + " has missing mapping!");
        return type.parse(v);
    }

    public <T> T getOrDefault(@NotNull String key, @NotNull BlockPropertyType<T> type, @Nullable T def) {
        String v = nodes.get(key);
        if (v == null) return def;
        T result = type.parse(v);
        return result == null ? def : result;
    }

    @NotNull
    public CollectionList<String> keys() { return nodes.keysList(); }

    public void forEach(@NotNull BiConsumer<? super String, ? super String> action) { nodes.forEach(action); }

    // raw access to map
    @NotNull
    public StringCollection<String> getNodes() { return nodes; }

    /* static methods */

    @NotNull
    public static BlockStatePropertyMap parse(@NotNull final String s) {
        BlockStatePropertyMap map = new BlockStatePropertyMap();
        ICollectionList.asList(s.replaceFirst("^.*?\\[(.*)]$", "$1").split(",")).forEach(key -> {
            String[] arr = key.split("=");
            if (arr.length == 1) {
                // xyz.acrylicstyle.tomeito_api.utils.Log.warn("Unable to parse property: " + key);
                return;
            }
            map.add(arr[0], arr[1]);
        });
        return map;
    }
}
