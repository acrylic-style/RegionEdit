package xyz.acrylicstyle.region.api;

import com.google.common.base.Preconditions;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import util.Validate;
import util.reflect.Ref;

import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

public final class MinecraftKey {
    /**
     * The namespace representing all inbuilt keys.
     */
    public static final String MINECRAFT = "minecraft";
    /**
     * The namespace representing all keys generated by Bukkit for backwards
     * compatibility measures.
     */
    public static final String BUKKIT = "bukkit";

    private static final Pattern VALID_NAMESPACE = Pattern.compile("[a-z0-9._-]+");
    private static final Pattern VALID_KEY = Pattern.compile("[a-z0-9/._-]+");

    private final String namespace;
    private final String key;

    /**
     * Create a key in a specific namespace.
     *
     * @param namespace namespace
     * @param key key
     */
    public MinecraftKey(@NotNull String namespace, @NotNull String key) {
        Preconditions.checkArgument(VALID_NAMESPACE.matcher(namespace).matches(), "Invalid namespace. Must be [a-z0-9._-]: %s", namespace);
        Preconditions.checkArgument(VALID_KEY.matcher(key).matches(), "Invalid key. Must be [a-z0-9/._-]: %s", key);

        this.namespace = namespace;
        this.key = key;

        String string = toString();
        Preconditions.checkArgument(string.length() < 256, "NamespacedKey must be less than 256 characters", string);
    }

    /**
     * Create a key in the plugin's namespace.
     * <p>
     * Namespaces may only contain lowercase alphanumeric characters, periods,
     * underscores, and hyphens.
     * <p>
     * Keys may only contain lowercase alphanumeric characters, periods,
     * underscores, hyphens, and forward slashes.
     *
     * @param plugin the plugin to use for the namespace
     * @param key the key to create
     */
    public MinecraftKey(@NotNull Plugin plugin, @NotNull String key) {
        Validate.notNull(plugin, "Plugin cannot be null");
        Validate.notNull(key, "Key cannot be null");

        this.namespace = plugin.getName().toLowerCase(Locale.ROOT);
        this.key = key.toLowerCase(Locale.ROOT);

        // Check validity after normalization
        Preconditions.checkArgument(VALID_NAMESPACE.matcher(this.namespace).matches(), "Invalid namespace. Must be [a-z0-9._-]: %s", this.namespace);
        Preconditions.checkArgument(VALID_KEY.matcher(this.key).matches(), "Invalid key. Must be [a-z0-9/._-]: %s", this.key);

        String string = toString();
        Preconditions.checkArgument(string.length() < 256, "NamespacedKey must be less than 256 characters (%s)", string);
    }

    /**
     * Create a key from existing NamespacedKey.
     * @param namespacedKey the key that will be imported to this key
     */
    public MinecraftKey(Object namespacedKey) {
        this.namespace = (String) Ref.forName("org.bukkit.NamespacedKey").getMethod("getNamespace").invoke(namespacedKey);
        this.key = (String) Ref.forName("org.bukkit.NamespacedKey").getMethod("getKey").invoke(namespacedKey);
    }

    @NotNull
    public String getNamespace() { return namespace; }

    @NotNull
    public String getKey() { return key; }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + this.namespace.hashCode();
        hash = 47 * hash + this.key.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final MinecraftKey other = (MinecraftKey) obj;
        return this.namespace.equals(other.namespace) && this.key.equals(other.key);
    }

    @Override
    public String toString() { return this.namespace + ":" + this.key; }

    /**
     * Return a new random key in the {@link #BUKKIT} namespace.
     *
     * @return new key
     */
    @Deprecated
    @NotNull
    public static MinecraftKey randomKey() {
        return new MinecraftKey(BUKKIT, UUID.randomUUID().toString());
    }

    /**
     * Get a key in the Minecraft namespace.
     *
     * @param key the key to use
     * @return new key in the Minecraft namespace
     */
    @NotNull
    public static MinecraftKey minecraft(@NotNull String key) {
        return new MinecraftKey(MINECRAFT, key);
    }
}
