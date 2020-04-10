package xyz.acrylicstyle.region.internal.utils;

import org.bukkit.command.TabCompleter;
import util.CollectionList;
import util.ICollectionList;

import java.util.ArrayList;
import java.util.List;

public abstract class TabCompleterHelper implements TabCompleter {
    protected final List<String> emptyList = new ArrayList<>();

    protected CollectionList<String> filterArgsList(List<String> list, String s) { return filterArgsList(ICollectionList.asList(list), s); }

    protected CollectionList<String> filterArgsList(CollectionList<String> list, String s) {
        return list.filter(s2 -> s2.toLowerCase().replaceAll(".*:(.*)", "$1").startsWith(s.toLowerCase().replaceAll(".*:(.*)", "$1")));
    }
}
