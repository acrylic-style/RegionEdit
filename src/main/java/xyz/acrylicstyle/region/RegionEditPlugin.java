package xyz.acrylicstyle.region;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import util.Collection;
import util.CollectionList;
import util.promise.Promise;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.exception.RegionEditException;
import xyz.acrylicstyle.region.api.manager.HistoryManager;
import xyz.acrylicstyle.region.api.operation.OperationStatus;
import xyz.acrylicstyle.region.api.player.UserSession;
import xyz.acrylicstyle.region.api.region.CuboidRegion;
import xyz.acrylicstyle.region.api.region.RegionSelection;
import xyz.acrylicstyle.region.api.selection.SelectionMode;
import xyz.acrylicstyle.region.commands.*;
import xyz.acrylicstyle.region.internal.player.UserSessionImpl;
import xyz.acrylicstyle.region.internal.utils.Reflection;
import xyz.acrylicstyle.tomeito_core.TomeitoLib;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Internal usage only
 */
public class RegionEditPlugin extends JavaPlugin implements RegionEdit, Listener {
    private Material selectionItem = null;

    private static HistoryManager historyManager = new HistoryManager();
    public static Collection<UUID, SelectionMode> selectionMode = new Collection<>();
    public static Collection<UUID, RegionSelection> regionSelection = new Collection<>();

    public static int blocksPerTick = 4096;

    @Override
    public void onEnable() {
        Bukkit.getServicesManager().register(RegionEdit.class, this, this, ServicePriority.Normal);
        Bukkit.getPluginManager().registerEvents(this, this);
        TomeitoLib.registerCommand("sel", new SelectionCommand());
        TomeitoLib.registerCommand("/set", new SetCommand());
        TomeitoLib.registerCommand("/limit", new LimitCommand());
        TomeitoLib.registerCommand("pos1", new Pos1Command());
        TomeitoLib.registerCommand("pos2", new Pos2Command());
        TomeitoLib.registerCommand("/replace", new ReplaceCommand());
        TomeitoLib.registerCommand("hpos1", new HPos1Command());
        TomeitoLib.registerCommand("hpos2", new HPos2Command());
        TomeitoLib.registerCommand("/cut", new CutCommand());
        TomeitoLib.registerCommand("undo", new UndoCommand());
        TomeitoLib.registerCommand("redo", new RedoCommand());
        TomeitoLib.registerCommand("cancel", new CancelCommand());
        selectionItem = Material.getMaterial(this.getConfig().getString("selection_item", "GOLD_AXE"));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getPlayer().hasPermission("regions.selection") && Reflection.getItemInHand(e.getPlayer()).getType() == selectionItem) {
            e.setCancelled(true);
            SelectionMode selectionMode = RegionEditPlugin.selectionMode.getOrDefault(e.getPlayer().getUniqueId(), SelectionMode.CUBOID);
            if (selectionMode == SelectionMode.CUBOID) {
                CuboidRegion cuboidRegion = (CuboidRegion) regionSelection.getOrDefault(e.getPlayer().getUniqueId(), new CuboidRegion(e.getBlock().getLocation(), e.getBlock().getLocation()));
                if (!cuboidRegion.getLocation2().getWorld().equals(e.getBlock().getWorld())) cuboidRegion = new CuboidRegion(e.getBlock().getLocation(), null);
                CuboidRegion reg = new CuboidRegion(e.getBlock().getLocation(), cuboidRegion.getLocation2());
                regionSelection.add(e.getPlayer().getUniqueId(), reg);
                CollectionList<Block> blocks = RegionEdit.getBlocks(reg.getLocation(), reg.getLocation2(), null, null);
                e.getPlayer().sendMessage(ChatColor.GREEN + "Selected region "
                        + ChatColor.YELLOW + "(" + loc2Str(reg.getLocation()) + " -> " + loc2Str(reg.getLocation2()) + ") "
                        + ChatColor.LIGHT_PURPLE + "(" + blocks.size() + " blocks)");
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        EquipmentSlot slot = Reflection.getHand(e);
        if (slot != null && slot != EquipmentSlot.HAND) return;
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getPlayer().hasPermission("regions.selection") && Reflection.getItemInHand(e.getPlayer()).getType() == selectionItem) {
                e.setCancelled(true);
                SelectionMode selectionMode = RegionEditPlugin.selectionMode.getOrDefault(e.getPlayer().getUniqueId(), SelectionMode.CUBOID);
                if (selectionMode == SelectionMode.CUBOID) {
                    CuboidRegion cuboidRegion = (CuboidRegion) regionSelection.getOrDefault(e.getPlayer().getUniqueId(), new CuboidRegion(e.getClickedBlock().getLocation(), e.getClickedBlock().getLocation()));
                    if (!cuboidRegion.getLocation().getWorld().equals(e.getClickedBlock().getWorld()))
                        cuboidRegion = new CuboidRegion(e.getClickedBlock().getLocation(), e.getClickedBlock().getLocation());
                    CuboidRegion reg = new CuboidRegion(cuboidRegion.getLocation(), e.getClickedBlock().getLocation());
                    regionSelection.add(e.getPlayer().getUniqueId(), reg);
                    CollectionList<Block> blocks = RegionEdit.getBlocks(reg.getLocation(), reg.getLocation2(), null, null);
                    e.getPlayer().sendMessage(ChatColor.GREEN + "Selected region "
                            + ChatColor.YELLOW + "(" + loc2Str(reg.getLocation()) + " -> " + loc2Str(reg.getLocation2()) + ") "
                            + ChatColor.LIGHT_PURPLE + "(" + blocks.size() + " blocks)");
                }
            }
        }
    }

    private String loc2Str(Location location) {
        if (location == null) return "null";
        return String.format(ChatColor.LIGHT_PURPLE + "%d, %d, %d" + ChatColor.YELLOW, location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Override
    @NotNull
    public UserSession getUserSession(@NotNull final UUID uuid) { return new UserSessionImpl(uuid); }

    @Override
    public int getBlocksPerTick() {
        return blocksPerTick;
    }

    @Override
    public void setBlocksPerTick(int blocks) {
        if (blocks <= 0) throw new RegionEditException("Blocks per ticks cannot be lower than 1");
        blocksPerTick = blocks;
    }

    @Override
    public HistoryManager getHistoryManager() { return historyManager; }

    // task id : cancelled state
    public static AtomicInteger taskId = new AtomicInteger();
    public static Collection<Integer, OperationStatus> tasks = new Collection<>();

    public static Collection<UUID, CollectionList<Integer>> playerTasks = new Collection<>();

    public static void setBlocks(Player player, CollectionList<Block> blocks, Material material, byte data) {
        double start = System.currentTimeMillis();
        historyManager.addEntry(player.getUniqueId(), blocks);
        if (!playerTasks.containsKey(player.getUniqueId())) playerTasks.add(player.getUniqueId(), new CollectionList<>());
        Plugin plugin = RegionEdit.getInstance();
        AtomicInteger i0 = new AtomicInteger();
        AtomicInteger i = new AtomicInteger();
        final int taskId = RegionEditPlugin.taskId.getAndIncrement();
        playerTasks.get(player.getUniqueId()).add(taskId);
        tasks.add(taskId, OperationStatus.RUNNING);
        player.sendMessage("" + ChatColor.RED + blocks.size() + ChatColor.GREEN + " blocks affected. " + ChatColor.LIGHT_PURPLE + " (Task ID: " + taskId + ")");
        blocks.forEach(block -> {
            new Promise<Object>() {
                @SuppressWarnings("deprecation")
                @Override
                public Object apply(Object o) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (tasks.get(taskId) == OperationStatus.CANCELLED) return;
                            block.setType(material);
                            block.setData(data);
                        }
                    }.runTaskLater(plugin, i0.get() % RegionEditPlugin.blocksPerTick == 0 ? i.getAndIncrement() : i.get());
                    return null;
                }
            }.queue();
            i0.incrementAndGet();
        });
        new BukkitRunnable() {
            @Override
            public void run() {
                if (tasks.get(taskId) == OperationStatus.CANCELLED) return;
                tasks.add(taskId, OperationStatus.FINISHED);
                double end = System.currentTimeMillis();
                double seconds = (end-start)/1000F;
                int bpt = (int) (blocks.size()/((float) (end-start)/50F));
                player.sendMessage(ChatColor.GREEN + "Operation completed. " + ChatColor.LIGHT_PURPLE + "(" + bpt + " blocks per ticks, took " + seconds + " seconds)");
            }
        }.runTaskLater(plugin, i.getAndIncrement());
    }

    public static void setBlocks(Player player, Collection<Location, Block> blocks) {
        Plugin plugin = RegionEdit.getInstance();
        AtomicInteger i0 = new AtomicInteger();
        AtomicInteger i = new AtomicInteger();
        blocks.forEach((loc, block) -> {
            new Promise<Object>() {
                @SuppressWarnings("deprecation")
                @Override
                public Object apply(Object o) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            loc.getBlock().setType(block.getType());
                            loc.getBlock().setData(block.getData());
                        }
                    }.runTaskLater(plugin, i0.get() % RegionEditPlugin.blocksPerTick == 0 ? i.getAndIncrement() : i.get());
                    return null;
                }
            }.queue();
            i0.incrementAndGet();
        });
        double start = System.currentTimeMillis();
        new BukkitRunnable() {
            @Override
            public void run() {
                double end = System.currentTimeMillis();
                double seconds = (end-start)/1000F;
                player.sendMessage(ChatColor.GREEN + "Operation completed. " + ChatColor.LIGHT_PURPLE + "(" + blocks.size() + " blocks, took " + seconds + " seconds)");
            }
        }.runTaskLater(plugin, i.getAndIncrement());
        player.sendMessage("" + ChatColor.RED + blocks.size() + ChatColor.GREEN + " blocks affected.");
    }
}
