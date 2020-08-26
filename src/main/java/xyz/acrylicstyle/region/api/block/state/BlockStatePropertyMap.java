package xyz.acrylicstyle.region.api.block.state;

import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.CollectionSet;
import util.ICollectionList;
import util.Validate;
import util.reflect.Ref;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.Map;
import java.util.function.Consumer;

public class BlockStatePropertyMap implements Serializable {
    public static final long serialVersionUID = 1L;
    private final ICollectionList<AbstractMap.SimpleEntry<String, String>> nodes;
    protected final String raw;

    public BlockStatePropertyMap(BlockStatePropertyMap blockStatePropertyMap) {
        this.nodes = blockStatePropertyMap.nodes;
        this.raw = blockStatePropertyMap.raw;
        this.cachedImpl = blockStatePropertyMap.cachedImpl;
    }

    public BlockStatePropertyMap(String raw) {
        nodes = new CollectionSet<>();
        this.raw = raw;
    }

    protected void add(@NotNull String key, @NotNull String value) { nodes.add(new AbstractMap.SimpleEntry<>(key, value)); }

    public <T> T get(@NotNull String key, @NotNull BlockPropertyType<T> type) {
        Map.Entry<String, String> v = nodes.filter(entry -> entry.getKey().equals(key)).first();
        Validate.notNull(v, key + " has missing mapping!");
        return type.parse(v.getValue());
    }

    public <T> T getOrDefault(@NotNull String key, @NotNull BlockPropertyType<T> type, @Nullable T def) {
        Map.Entry<String, String> v = nodes.filter(entry -> entry.getKey().equals(key)).first();
        if (v == null) return def;
        T result = type.parse(v.getValue());
        return result == null ? def : result;
    }

    public void forEach(@NotNull Consumer<AbstractMap.SimpleEntry<String, String>> action) { nodes.forEach(action); }

    // raw access to map
    @NotNull
    public ICollectionList<AbstractMap.SimpleEntry<String, String>> getNodes() { return nodes; }

    private BlockStatePropertyMap cachedImpl;
    public BlockStatePropertyMap implementMethods() {
        if (this.cachedImpl != null) return this.cachedImpl;
        this.cachedImpl = RegionEdit.getInstance().implementMethods(this);
        return this.cachedImpl;
    }

    public void apply(Block block) {
        implementMethods().apply(block);
    }

    public Object getIBlockData(MaterialData data) {
        return implementMethods().getIBlockData(data);
    }

    /* static methods */

    @NotNull
    public static BlockStatePropertyMap parse(@NotNull final String s) {
        BlockStatePropertyMap map = new BlockStatePropertyMap(s);
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

    /**
     * Get block state properties for the block.
     * @param block the block
     * @return PropertyMap, null if not supported
     */
    @Nullable
    public static BlockStatePropertyMap from(final Block block) {
        Method method = getNMS();
        if (method == null) return null;
        try {
            return parse(method.invoke(block).toString());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    // should always available
    private static final Class<?> CraftBlock = Ref.forName(ReflectionUtil.getCraftBukkitPackage() + ".block.CraftBlock").getClazz();

    private static boolean checked = false;
    private static Method cachedMethod = null;

    private static Method getNMS() {
        if (checked) return cachedMethod;
        try {
            cachedMethod = CraftBlock.getDeclaredMethod("getNMS");
            cachedMethod.setAccessible(true);
        } catch (NoSuchMethodException ignore) {}
        checked = true;
        return cachedMethod;
    }
}
