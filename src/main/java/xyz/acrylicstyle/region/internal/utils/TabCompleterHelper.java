package xyz.acrylicstyle.region.internal.utils;

import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;

public abstract class TabCompleterHelper extends xyz.acrylicstyle.tomeito_api.utils.TabCompleterHelper implements TabCompleter {
    protected static final List<String> emptyList = Collections.emptyList();
}
