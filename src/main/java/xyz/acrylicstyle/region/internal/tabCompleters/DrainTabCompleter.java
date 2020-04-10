package xyz.acrylicstyle.region.internal.tabCompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import xyz.acrylicstyle.region.internal.utils.TabCompleterHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DrainTabCompleter extends TabCompleterHelper {
    private List<String> list = create100List();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) return list;
        if (args.length == 1) return filterArgsList(list, args[0]);
        if (args.length == 2) return filterArgsList(Arrays.asList("kelp", "lava", "seagrass"), args[1]);
        return emptyList;
    }

    private List<String> create100List() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) list.add("" + i);
        return list;
    }
}
