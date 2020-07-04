package xyz.acrylicstyle.region.internal.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import util.ArgumentParser;
import util.CollectionList;
import util.ICollectionList;
import xyz.acrylicstyle.region.RegionEditPlugin;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.region.CuboidRegion;
import xyz.acrylicstyle.tomeito_api.TomeitoAPI;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DistributionCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        ArgumentParser parser = new ArgumentParser(ICollectionList.asList(args).join(" "));
        CollectionList<Material> exclude =  new CollectionList<>();
        if (parser.containsKey("exclude")) {
            ICollectionList.asList(parser.getString("exclude").toUpperCase().split(",")).forEach(s -> {
                Material material = Material.getMaterial(s);
                if (material != null) exclude.add(material);
            });
        }
        CuboidRegion region = (CuboidRegion) RegionEditPlugin.regionSelection.getOrDefault(player.getUniqueId(), new CuboidRegion(null, null));
        if (!region.isValid()) {
            player.sendMessage(ChatColor.RED + "You must make selection first.");
            return;
        }
        assert region.getLocation() != null;
        RegionEdit.getBlocksAsync(region.getLocation(), region.getLocation2(), null, null, (blocks, throwable) -> {
            CollectionList<Material> materials = blocks.map(Block::getType);
            CollectionList<Map.Entry<Material, Map.Entry<Double, Integer>>> material2 = materials
                    .unique()
                    .filter(m -> !exclude.contains(m))
                    .toMap(material -> material, m -> materials.filter(m2 -> !exclude.contains(m2)).distributionEntry(m))
                    .toEntryList();
            double ex = Math.round(materials.filter(exclude::contains).size() / (double) materials.size() * 100 * 1000) / 1000D;
            material2.sort((a, b) -> b.getValue().getValue() - a.getValue().getValue());
            CollectionList<String> messages = new CollectionList<>();
            messages.add(ChatColor.GRAY + "Total blocks: " + blocks.size() + ", excluded " + ex + "%");
            AtomicInteger index = new AtomicInteger();
            material2.forEach(e -> {
                if (index.get() < 30) {
                    String name = TomeitoAPI.getFriendlyName(e.getKey());
                    double dist = Math.round(e.getValue().getKey() * 100 * 1000D) / 1000D;
                    int amount = e.getValue().getValue();
                    StringBuilder line = new StringBuilder(ChatColor.YELLOW + "" + amount + ChatColor.GOLD);
                    while (line.length() <= 8+4) line.append(" ");
                    line.append("(").append(dist).append("%) ").append(ChatColor.LIGHT_PURPLE).append(name);
                    messages.add(line.toString());
                }
                index.getAndIncrement();
            });
            messages.forEach(player::sendMessage);
        });
    }
}
