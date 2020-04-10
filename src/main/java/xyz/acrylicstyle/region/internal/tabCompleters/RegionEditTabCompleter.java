package xyz.acrylicstyle.region.internal.tabCompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import xyz.acrylicstyle.region.internal.utils.TabCompleterHelper;

import java.util.Arrays;
import java.util.List;

public class RegionEditTabCompleter extends TabCompleterHelper {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) return Arrays.asList("help", "version", "reload", "commands", "compatibility");
        if (args.length == 1) return filterArgsList(Arrays.asList("help", "version", "reload", "commands", "compatibility"), args[0]);
        return emptyList;
    }
}
