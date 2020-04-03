package xyz.acrylicstyle.region.internal.commands;

import org.bukkit.ChatColor;
import util.CollectionList;
import util.ICollectionList;

import java.util.Collections;
import java.util.List;

public class CommandDescription {
    private final String name;
    private final CollectionList<String> permissions;
    private final CollectionList<String> description;

    public CommandDescription(String name, List<String> permissions, String... description) {
        this.name = name;
        this.permissions = permissions != null ? ICollectionList.asList(permissions) : null;
        this.description = ICollectionList.asList(description);
    }

    public CommandDescription(String name, String permissions, String... description) {
        this(name, Collections.singletonList(permissions), description);
    }

    public String getName() {
        return name;
    }

    public CollectionList<String> getPermissions() {
        return permissions;
    }

    public String getPermissionsAsString() {
        if (getPermissions() == null || getPermissions().join("").equals("")) return ChatColor.YELLOW + "None";
        return getPermissions().map(s -> ChatColor.LIGHT_PURPLE + " - " + s).join("\n");
    }

    public CollectionList<String> getDescription() {
        return description;
    }
}
