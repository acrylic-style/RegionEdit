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
import util.ReflectionHelper;
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
import xyz.acrylicstyle.region.internal.commands.CommandDescription;
import xyz.acrylicstyle.region.internal.commands.CommandDescriptionManager;
import xyz.acrylicstyle.region.internal.player.UserSessionImpl;
import xyz.acrylicstyle.region.internal.utils.Compatibility;
import xyz.acrylicstyle.region.internal.utils.Reflection;
import xyz.acrylicstyle.tomeito_core.TomeitoLib;
import xyz.acrylicstyle.tomeito_core.utils.Log;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Internal usage only
 */
public class RegionEditPlugin extends JavaPlugin implements RegionEdit, Listener {
    private Material selectionItem = null;
    private Material navigationItem = null;

    private static HistoryManager historyManager = new HistoryManager();
    public static Collection<UUID, SelectionMode> selectionMode = new Collection<>();
    public static Collection<UUID, RegionSelection> regionSelection = new Collection<>();
    public static CommandDescriptionManager commandDescriptionManager = new CommandDescriptionManager();

    public static int blocksPerTick = 4096;

    @Override
    public void onEnable() {
        Bukkit.getServicesManager().register(RegionEdit.class, this, this, ServicePriority.Normal);
        Bukkit.getPluginManager().registerEvents(this, this);
        TomeitoLib.registerCommand("regionedit", new RegionEditCommand());
        TomeitoLib.registerCommand("/help", new HelpCommand());
        TomeitoLib.registerCommand("sel", new SelectionCommand());
        TomeitoLib.registerCommand("/set", new SetCommand());
        TomeitoLib.registerCommand("/limit", new LimitCommand());
        TomeitoLib.registerCommand("/pos1", new Pos1Command());
        TomeitoLib.registerCommand("/pos2", new Pos2Command());
        TomeitoLib.registerCommand("/replace", new ReplaceCommand());
        TomeitoLib.registerCommand("/hpos1", new HPos1Command());
        TomeitoLib.registerCommand("/hpos2", new HPos2Command());
        TomeitoLib.registerCommand("/cut", new CutCommand());
        TomeitoLib.registerCommand("/undo", new UndoCommand());
        TomeitoLib.registerCommand("/redo", new RedoCommand());
        TomeitoLib.registerCommand("/cancel", new CancelCommand());
        TomeitoLib.registerCommand("/drain", new DrainCommand());
        TomeitoLib.registerCommand("/expand", new ExpandCommand());
        commandDescriptionManager.add("//help", new CommandDescription("//help [page]", "regions.help", "Shows all RegionEdit commands."));
        commandDescriptionManager.add("/;", new CommandDescription("//sel [cuboid]", "", "Clears selection or switches selection mode."));
        commandDescriptionManager.add("//sel", new CommandDescription("//sel [cuboid]", "", "Clears selection or switches selection mode."));
        commandDescriptionManager.add("//limit", new CommandDescription("//limit [number]", "regions.limit", "Limits blocks per ticks"));
        commandDescriptionManager.add("//pos1", new CommandDescription("//pos1", "regions.selection", "Set position 1 at player's location."));
        commandDescriptionManager.add("//pos2", new CommandDescription("//pos2", "regions.selection", "Set position 2 at player's location."));
        commandDescriptionManager.add("//hpos1", new CommandDescription("//hpos1", "regions.selection", "Set position 1 at block player's is looking at."));
        commandDescriptionManager.add("//hpos2", new CommandDescription("//hpos2", "regions.selection", "Set position 2 at block player's is looking at."));
        commandDescriptionManager.add("//cut", new CommandDescription("//cut", "regions.cut", "Removes blocks at specified region."));
        commandDescriptionManager.add("//replace", new CommandDescription("//replace [before] [after]", "regions.replace", "Replace blocks at specified region."));
        commandDescriptionManager.add("//set", new CommandDescription("//set [block]", "regions.set", "Places blocks at specified region."));
        commandDescriptionManager.add("//undo", new CommandDescription("//undo", "regions.undo", "Rollbacks action."));
        commandDescriptionManager.add("//redo", new CommandDescription("//redo", "regions.redo", "Rollbacks undo action."));
        commandDescriptionManager.add("//drain", new CommandDescription("//drain [radius] [lava]", "regions.drain", "Drains water near you."));
        commandDescriptionManager.add("//cancel", new CommandDescription("//cancel [task id/all]",
                Arrays.asList("regions.cancel", "regions.cancel.a -> self", "regions.cancel.b -> others", "regions.cancel.c -> all"),
                "Cancels current operation."));
        commandDescriptionManager.add("//expand", new CommandDescription("//expand <<<number> <up/down/east/south/west/north>>/<vert>>", "regions.selection", "Expands selection area by <number>."));
        selectionItem = Material.getMaterial(this.getConfig().getString("selection_item", "GOLD_AXE"));
        navigationItem = Material.getMaterial(this.getConfig().getString("navigation_item", "COMPASS"));
        if (!Compatibility.checkPlayerInventory_getItemInHand()) {
            Log.info("[Compatibility] Not Found Inventory#getItemInHand (1.13+)");
        } else {
            Log.info("[Compatibility] Found Inventory#getItemInHand (1.8 - 1.12.2)");
        }
        if (!Compatibility.checkPlayerInteractEvent_getHand()) {
            Log.info("[Compatibility] Not Found PlayerInteractEvent#getHand (1.8)");
        } else {
            Log.info("[Compatibility] Found PlayerInteractEvent#getHand (1.9+)");
        }
        if (!Compatibility.checkBlock_getData()) {
            Log.info("[Compatibility] Not Found Block#getData (1.13+)");
        } else {
            Log.info("[Compatibility] Found Block#getData (1.8 - 1.12.2)");
        }
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
                selectRegion(reg, e.getPlayer());
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
                    selectRegion(reg, e.getPlayer());
                }
            }
        }
        if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
            if (e.getPlayer().hasPermission("regions.navigation") && Reflection.getItemInHand(e.getPlayer()).getType() == navigationItem) {
                Block block = e.getPlayer().getTargetBlock((Set<Material>) null, 500);
                e.setCancelled(true);
                if (block == null || block.getType() == Material.AIR) {
                    e.getPlayer().sendMessage(ChatColor.RED + "Couldn't find block! (or too far)");
                    return;
                }
                int i = 0;
                while (block.getLocation().add(0, i+1, 0).getBlock().getType() != Material.AIR) i++;
                Location location = block.getLocation().clone().add(0.5, i+1, 0.5);
                location.setYaw(e.getPlayer().getLocation().getYaw());
                location.setPitch(e.getPlayer().getLocation().getPitch());
                e.getPlayer().teleport(location);
            }
        }
    }

    private void selectRegion(@NotNull CuboidRegion reg, @NotNull Player player) {
        regionSelection.add(player.getUniqueId(), reg);
        showCurrentRegion(player);
    }

    public static void showCurrentRegion(Player player) {
        CuboidRegion reg = (CuboidRegion) regionSelection.get(player.getUniqueId());
        CollectionList<Block> blocks = RegionEdit.getBlocks(reg.getLocation(), reg.getLocation2(), null, null);
        player.sendMessage(ChatColor.GREEN + "Selected region "
                + ChatColor.YELLOW + "(" + loc2Str(reg.getLocation()) + " -> " + loc2Str(reg.getLocation2()) + ") "
                + ChatColor.LIGHT_PURPLE + "(" + blocks.size() + " blocks)");
    }

    public static String loc2Str(Location location) {
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

    public static void setBlocks(Player player, @NotNull CollectionList<Block> blocks, Material material, byte data) {
        double start = System.currentTimeMillis();
        CollectionList<xyz.acrylicstyle.region.api.block.Block> blocks2 = blocks.map(xyz.acrylicstyle.region.api.block.Block::new);
        if (!playerTasks.containsKey(player.getUniqueId())) playerTasks.add(player.getUniqueId(), new CollectionList<>());
        Plugin plugin = RegionEdit.getInstance();
        AtomicInteger i0 = new AtomicInteger();
        AtomicInteger i = new AtomicInteger();
        final int taskId = RegionEditPlugin.taskId.getAndIncrement();
        playerTasks.get(player.getUniqueId()).add(taskId);
        tasks.add(taskId, OperationStatus.RUNNING);
        player.sendMessage("" + ChatColor.RED + blocks.size() + ChatColor.GREEN + " blocks affected. " + ChatColor.LIGHT_PURPLE + " (Task ID: " + taskId + ")");
        blocks.forEach(block -> {
            new Thread(() -> new BukkitRunnable() {
                @SuppressWarnings("deprecation")
                @Override
                public void run() {
                    if (tasks.get(taskId) == OperationStatus.CANCELLED) return;
                    block.setType(material);
                    if (!Compatibility.checkBlock_getData()) block.setData(data); // use Block#setData only if it presents
                }
            }.runTaskLater(plugin, i0.get() % RegionEditPlugin.blocksPerTick == 0 ? i.getAndIncrement() : i.get())).start();
            i0.incrementAndGet();
        });
        new BukkitRunnable() {
            @Override
            public void run() {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        historyManager.addEntry(player.getUniqueId(), blocks2);
                    }
                }.runTaskLater(RegionEdit.getInstance(), 10);
                completeOperation(blocks.size(), taskId, start, player);
            }
        }.runTaskLater(plugin, i.getAndIncrement());
    }

    private static void completeOperation(int size, int taskId, double start, Player player) {
        if (tasks.get(taskId) == OperationStatus.CANCELLED) return;
        tasks.add(taskId, OperationStatus.FINISHED);
        double end = System.currentTimeMillis();
        double seconds = (end-start)/1000F;
        int bpt = (int) (size /((float) (end-start)/50F));
        player.sendMessage(ChatColor.GREEN + "Operation completed. " + ChatColor.LIGHT_PURPLE + "(" + bpt + " blocks per ticks, took " + seconds + " seconds)");
    }

    public static void setBlocks(Player player, Collection<Location, xyz.acrylicstyle.region.api.block.Block> blocks) {
        double start = System.currentTimeMillis();
        // historyManager.addEntry(player.getUniqueId(), blocks);
        if (!playerTasks.containsKey(player.getUniqueId())) playerTasks.add(player.getUniqueId(), new CollectionList<>());
        Plugin plugin = RegionEdit.getInstance();
        AtomicInteger i0 = new AtomicInteger();
        AtomicInteger i = new AtomicInteger();
        final int taskId = RegionEditPlugin.taskId.getAndIncrement();
        playerTasks.get(player.getUniqueId()).add(taskId);
        tasks.add(taskId, OperationStatus.RUNNING);
        player.sendMessage("" + ChatColor.RED + blocks.size() + ChatColor.GREEN + " blocks affected. " + ChatColor.LIGHT_PURPLE + " (Task ID: " + taskId + ")");
        blocks.forEach((loc, block) -> {
            new Promise<Object>() {
                @SuppressWarnings("deprecation")
                @Override
                public Object apply(Object o) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (tasks.get(taskId) == OperationStatus.CANCELLED) return;
                            loc.getBlock().setType(block.getType());
                            if (ReflectionHelper.findMethod(Block.class, "getData") != null) loc.getBlock().setData(block.getData());
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
                completeOperation(blocks.size(), taskId, start, player);
            }
        }.runTaskLater(plugin, i.getAndIncrement());
    }
}
