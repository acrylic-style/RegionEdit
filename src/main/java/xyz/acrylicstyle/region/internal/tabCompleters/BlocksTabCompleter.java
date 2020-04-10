package xyz.acrylicstyle.region.internal.tabCompleters;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import util.ICollectionList;
import xyz.acrylicstyle.region.internal.utils.TabCompleterHelper;

import java.util.List;
import java.util.function.Function;

public class BlocksTabCompleter extends TabCompleterHelper {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) return ICollectionList
                .asList(Material.values())
                .filter(Material::isBlock)
                .map(Enum::name)
                .map((Function<String, String>) String::toLowerCase);
        if (args.length == 1) return filterArgsList(
                ICollectionList
                        .asList(Material.values())
                        .filter(Material::isBlock)
                        .map(Enum::name)
                        .map((Function<String, String>) String::toLowerCase),
                args[0]
        );
        return emptyList;
    }
}
