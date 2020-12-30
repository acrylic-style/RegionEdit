package xyz.acrylicstyle.region;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Collection;
import util.CollectionList;
import util.CollectionSet;
import util.ICollectionList;
import util.memory.ReservedMemory;
import xyz.acrylicstyle.region.api.AsyncCatcher;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.block.state.BlockState;
import xyz.acrylicstyle.region.api.operation.OperationStatus;
import xyz.acrylicstyle.region.api.player.SuperPickaxeMode;
import xyz.acrylicstyle.region.api.player.UserSession;
import xyz.acrylicstyle.region.api.region.CuboidRegion;
import xyz.acrylicstyle.region.api.selection.SelectionMode;
import xyz.acrylicstyle.region.api.util.BlockPos;
import xyz.acrylicstyle.region.internal.RegionEditImpl;
import xyz.acrylicstyle.region.internal.block.BlockUtil;
import xyz.acrylicstyle.region.internal.block.RegionBlock;
import xyz.acrylicstyle.region.internal.command.CommandDescription;
import xyz.acrylicstyle.region.internal.commands.CancelCommand;
import xyz.acrylicstyle.region.internal.commands.ChunkCommand;
import xyz.acrylicstyle.region.internal.commands.CutCommand;
import xyz.acrylicstyle.region.internal.commands.DistributionCommand;
import xyz.acrylicstyle.region.internal.commands.DrainCommand;
import xyz.acrylicstyle.region.internal.commands.DrawSelCommand;
import xyz.acrylicstyle.region.internal.commands.ExpandCommand;
import xyz.acrylicstyle.region.internal.commands.FastCommand;
import xyz.acrylicstyle.region.internal.commands.HPos1Command;
import xyz.acrylicstyle.region.internal.commands.HPos2Command;
import xyz.acrylicstyle.region.internal.commands.HelpCommand;
import xyz.acrylicstyle.region.internal.commands.LimitCommand;
import xyz.acrylicstyle.region.internal.commands.PasteCommand;
import xyz.acrylicstyle.region.internal.commands.Pos1Command;
import xyz.acrylicstyle.region.internal.commands.Pos2Command;
import xyz.acrylicstyle.region.internal.commands.RedoCommand;
import xyz.acrylicstyle.region.internal.commands.RegionEditCommand;
import xyz.acrylicstyle.region.internal.commands.ReplaceCommand;
import xyz.acrylicstyle.region.internal.commands.SchematicCommand;
import xyz.acrylicstyle.region.internal.commands.SelectionCommand;
import xyz.acrylicstyle.region.internal.commands.SetCommand;
import xyz.acrylicstyle.region.internal.commands.SuperPickaxeCommand;
import xyz.acrylicstyle.region.internal.commands.UndoCommand;
import xyz.acrylicstyle.region.internal.commands.UnstuckCommand;
import xyz.acrylicstyle.region.internal.commands.WandCommand;
import xyz.acrylicstyle.region.internal.listener.CUIChannelListener;
import xyz.acrylicstyle.region.internal.nms.Chunk;
import xyz.acrylicstyle.region.internal.player.UserSessionImpl;
import xyz.acrylicstyle.region.internal.tabCompleters.BlocksTabCompleter;
import xyz.acrylicstyle.region.internal.tabCompleters.DrainTabCompleter;
import xyz.acrylicstyle.region.internal.tabCompleters.RegionEditTabCompleter;
import xyz.acrylicstyle.region.internal.tabCompleters.ReplaceBlocksTabCompleter;
import xyz.acrylicstyle.region.internal.utils.BukkitVersion;
import xyz.acrylicstyle.region.internal.utils.Compatibility;
import xyz.acrylicstyle.region.internal.utils.Reflection;
import xyz.acrylicstyle.tomeito_api.TomeitoAPI;
import xyz.acrylicstyle.tomeito_api.utils.Log;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Internal usage only
 */
@SuppressWarnings("UnstableApiUsage")
public class RegionEditPlugin extends RegionEditImpl implements RegionEdit, Listener {
    public static final ReservedMemory reserve = new ReservedMemory(1024*1024*128); // 128mb?
    public static final String COMMAND_PREFIX = "/";
    public static final String CUI = "worldedit:cui";
    public static final String CUI_LEGACY = "WECUI";

    @Override
    public void onEnable() {
        if (VERSION == BukkitVersion.UNKNOWN) {
            Log.as("RegionEdit").severe("You are using an unknown/unsupported bukkit version.");
            Log.as("RegionEdit").severe("Disabling plugin as this plugin will very likely break.");
            Log.as("RegionEdit").severe("Supported versions are: 1.8 - 1.16.2");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Log.info("Registering events");
        Bukkit.getServicesManager().register(RegionEdit.class, this, this, ServicePriority.Normal);
        Bukkit.getPluginManager().registerEvents(this, this);
        Log.info("Registering commands");
        TomeitoAPI.registerCommand("regionedit", new RegionEditCommand());
        TomeitoAPI.registerCommand(COMMAND_PREFIX + "wand", new WandCommand());
        TomeitoAPI.registerCommand(COMMAND_PREFIX + "distr", new DistributionCommand());
        TomeitoAPI.registerCommand(COMMAND_PREFIX + "help", new HelpCommand());
        TomeitoAPI.registerCommand(COMMAND_PREFIX + "sel", new SelectionCommand());
        TomeitoAPI.registerCommand(COMMAND_PREFIX + "set", new SetCommand());
        TomeitoAPI.registerCommand(COMMAND_PREFIX + "limit", new LimitCommand());
        TomeitoAPI.registerCommand(COMMAND_PREFIX + "pos1", new Pos1Command());
        TomeitoAPI.registerCommand(COMMAND_PREFIX + "pos2", new Pos2Command());
        TomeitoAPI.registerCommand(COMMAND_PREFIX + "replace", new ReplaceCommand());
        TomeitoAPI.registerCommand(COMMAND_PREFIX + "hpos1", new HPos1Command());
        TomeitoAPI.registerCommand(COMMAND_PREFIX + "hpos2", new HPos2Command());
        TomeitoAPI.registerCommand(COMMAND_PREFIX + "cut", new CutCommand());
        TomeitoAPI.registerCommand(COMMAND_PREFIX + "undo", new UndoCommand());
        TomeitoAPI.registerCommand(COMMAND_PREFIX + "redo", new RedoCommand());
        TomeitoAPI.registerCommand(COMMAND_PREFIX + "cancel", new CancelCommand());
        TomeitoAPI.registerCommand(COMMAND_PREFIX + "drain", new DrainCommand());
        TomeitoAPI.registerCommand(COMMAND_PREFIX + "expand", new ExpandCommand());
        TomeitoAPI.registerCommand(COMMAND_PREFIX + "fast", new FastCommand());
        TomeitoAPI.registerCommand(COMMAND_PREFIX + "unstuck", new UnstuckCommand());
        TomeitoAPI.registerCommand(COMMAND_PREFIX + "chunk", new ChunkCommand());
        TomeitoAPI.registerCommand(COMMAND_PREFIX + "drawsel", new DrawSelCommand());
        TomeitoAPI.registerCommand(COMMAND_PREFIX + "sp", new SuperPickaxeCommand());
        TomeitoAPI.registerCommand(COMMAND_PREFIX + "schem", new SchematicCommand());
        TomeitoAPI.registerCommand(COMMAND_PREFIX + "paste", new PasteCommand());
        Log.info("Registering tab completers");
        Bukkit.getPluginCommand("regionedit").setTabCompleter(new RegionEditTabCompleter());
        Bukkit.getPluginCommand(COMMAND_PREFIX + "set").setTabCompleter(new BlocksTabCompleter());
        Bukkit.getPluginCommand(COMMAND_PREFIX + "replace").setTabCompleter(new ReplaceBlocksTabCompleter());
        Bukkit.getPluginCommand(COMMAND_PREFIX + "drain").setTabCompleter(new DrainTabCompleter());
        Log.info("Registering command help");
        commandDescriptionManager.add("//help", new CommandDescription("//help [page]", "regionedit.help", "Shows all RegionEdit commands."));
        commandDescriptionManager.add("/re", new CommandDescription("/re <help/version/reload/commands/compatibility>",
                Arrays.asList("regionedit.compatibility", "regionedit.reload", "regionedit.help"),
                "Displays information about RegionEdit."));
        commandDescriptionManager.add("/" + COMMAND_PREFIX + ";", new CommandDescription("/" + COMMAND_PREFIX + "sel [cuboid]", "", "Clears selection or switches selection mode."));
        commandDescriptionManager.add("/" + COMMAND_PREFIX + "sel", new CommandDescription("/" + COMMAND_PREFIX + "sel [cuboid]", "", "Clears selection or switches selection mode."));
        commandDescriptionManager.add("/" + COMMAND_PREFIX + "limit", new CommandDescription("/" + COMMAND_PREFIX + "limit [number]", "regionedit.limit", "Limits blocks per ticks"));
        commandDescriptionManager.add("/" + COMMAND_PREFIX + "pos1", new CommandDescription("/" + COMMAND_PREFIX + "pos1", "regionedit.selection", "Set position 1 at player's location."));
        commandDescriptionManager.add("/" + COMMAND_PREFIX + "pos2", new CommandDescription("/" + COMMAND_PREFIX + "pos2", "regionedit.selection", "Set position 2 at player's location."));
        commandDescriptionManager.add("/" + COMMAND_PREFIX + "hpos1", new CommandDescription("/" + COMMAND_PREFIX + "hpos1", "regionedit.selection", "Set position 1 at block player's is looking at."));
        commandDescriptionManager.add("/" + COMMAND_PREFIX + "hpos2", new CommandDescription("/" + COMMAND_PREFIX + "hpos2", "regionedit.selection", "Set position 2 at block player's is looking at."));
        commandDescriptionManager.add("/" + COMMAND_PREFIX + "cut", new CommandDescription("/" + COMMAND_PREFIX + "cut", "regionedit.cut", "Removes blocks at specified region."));
        commandDescriptionManager.add("/" + COMMAND_PREFIX + "replace", new CommandDescription("/" + COMMAND_PREFIX + "replace [before] [after]", "regionedit.replace", "Replace blocks at specified region."));
        commandDescriptionManager.add("/" + COMMAND_PREFIX + "set", new CommandDescription("/" + COMMAND_PREFIX + "set [block]", "regionedit.set", "Places blocks at specified region."));
        commandDescriptionManager.add("/" + COMMAND_PREFIX + "undo", new CommandDescription("/" + COMMAND_PREFIX + "undo", "regionedit.undo", "Rollbacks action."));
        commandDescriptionManager.add("/" + COMMAND_PREFIX + "redo", new CommandDescription("/" + COMMAND_PREFIX + "redo", "regionedit.redo", "Rollbacks undo action."));
        commandDescriptionManager.add("/" + COMMAND_PREFIX + "drain", new CommandDescription("/" + COMMAND_PREFIX + "drain [radius] [lava]", "regionedit.drain", "Drains water near you."));
        commandDescriptionManager.add("/" + COMMAND_PREFIX + "cancel", new CommandDescription("/" + COMMAND_PREFIX + "cancel [task id/all]",
                Arrays.asList("regionedit.cancel", "regionedit.cancel.a -> self", "regionedit.cancel.b -> others", "regionedit.cancel.c -> all"),
                "Cancels current operation."));
        commandDescriptionManager.add("/" + COMMAND_PREFIX + "expand", new CommandDescription("/" + COMMAND_PREFIX + "expand <<<number> <up/down/east/south/west/north>>/<vert>>", "regionedit.expand", "Expands selection area by <number>."));
        commandDescriptionManager.add("/" + COMMAND_PREFIX + "fast", new CommandDescription("/" + COMMAND_PREFIX + "fast", "regionedit.fast", "Toggles fast mode.", "Fast mode disables some physics on operation."));
        commandDescriptionManager.add("/" + COMMAND_PREFIX + "unstuck", new CommandDescription("/" + COMMAND_PREFIX + "unstuck", "regionedit.unstuck", "Get out of stuck."));
        commandDescriptionManager.add("/" + COMMAND_PREFIX + "chunk", new CommandDescription("/" + COMMAND_PREFIX + "chunk", "regionedit.selection", "Selects an entire chunk."));
        commandDescriptionManager.add("/" + COMMAND_PREFIX + "wand", new CommandDescription("/" + COMMAND_PREFIX + "wand", "regionedit.wand", "Gives player a wand (item) to get started with RegionEdit."));
        commandDescriptionManager.add("/" + COMMAND_PREFIX + "distr", new CommandDescription("/" + COMMAND_PREFIX + "distr [--exclude=blocks,separated,by,comma]", "regionedit.distr", "Shows block distribution."));
        commandDescriptionManager.add("/" + COMMAND_PREFIX + "sp", new CommandDescription("/" + COMMAND_PREFIX + "sp <area <radius>/single/drop <radius>/off>", "regionedit.superpickaxe", "Toggles super pickaxe mode."));
        commandDescriptionManager.add("/" + COMMAND_PREFIX + "drawsel", new CommandDescription("/" + COMMAND_PREFIX + "drawsel", "regionedit.drawsel", "Toggles draws selection mode"));
        commandDescriptionManager.add("/" + COMMAND_PREFIX + "schem", new CommandDescription("/" + COMMAND_PREFIX + "schem <load|list>", "regionedit.schem", "Manages schematics."));
        commandDescriptionManager.add("/" + COMMAND_PREFIX + "paste", new CommandDescription("/" + COMMAND_PREFIX + "paste", "regionedit.paste", "Pastes blocks in the clipboard."));
        selectionItem = Material.getMaterial(this.getConfig().getString("selection_item", Compatibility.getGoldenAxe().name()));
        navigationItem = Material.getMaterial(this.getConfig().getString("navigation_item", "COMPASS"));
        Log.info("Detected server version: " + VERSION.getName());
        Bukkit.getMessenger().registerIncomingPluginChannel(this, CUI, new CUIChannelListener(this));
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, CUI);
        if (!VERSION.atLeast(BukkitVersion.v1_13)) Bukkit.getMessenger().registerIncomingPluginChannel(this, CUI_LEGACY, new CUIChannelListener(this));
        if (!VERSION.atLeast(BukkitVersion.v1_13)) Bukkit.getMessenger().registerOutgoingPluginChannel(this, CUI_LEGACY);
        for (Player p : Bukkit.getOnlinePlayers()) onPlayerJoin(new PlayerJoinEvent(p, ""));
        /*
        new BukkitRunnable() {
            @Override
            public void run() {
                //noinspection RedundantCast
                sessions
                        .clone()
                        .filter(session -> session.isDrawSelection() && session.getSelectionMode() == SelectionMode.CUBOID && session.getCuboidRegion().isValid())
                        .toList((u, s) -> u)
                        .map((Function<UUID, Player>) Bukkit::getPlayer)
                        .nonNull()
                        .toMap(p -> p, p -> getUserSession(p))
                        .forEach((player, session) -> {
                            RegionEdit.drawParticleLine(player, Objects.requireNonNull(session.getCuboidRegion().getLocation()), session.getCuboidRegion().getLocation2());
                        });
            }
        }.runTaskTimer(this, 10, 10);
        */
    }

    @Override
    public void onDisable() {
        reserve.free(); // release reserved memory
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        if (sessions.containsKey(e.getUniqueId())) sessions.get(e.getUniqueId()).setCUISupport(false);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!sessions.containsKey(e.getPlayer().getUniqueId())) sessions.add(e.getPlayer().getUniqueId(), new UserSessionImpl(this, e.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getPlayer().hasPermission("regionedit.selection") && Reflection.getItemInHand(e.getPlayer()).getType() == selectionItem) {
            e.setCancelled(true);
            SelectionMode selectionMode = RegionEditPlugin.selectionMode.getOrDefault(e.getPlayer().getUniqueId(), SelectionMode.CUBOID);
            if (selectionMode == SelectionMode.CUBOID) {
                CuboidRegion cuboidRegion = (CuboidRegion) regionSelection.getOrDefault(e.getPlayer().getUniqueId(), new CuboidRegion(e.getBlock().getLocation(), e.getBlock().getLocation()));
                if (!Objects.requireNonNull(cuboidRegion.getLocation2()).getWorld().equals(e.getBlock().getWorld())) cuboidRegion = new CuboidRegion(e.getBlock().getLocation(), null);
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
            if (e.getPlayer().hasPermission("regionedit.selection") && Reflection.getItemInHand(e.getPlayer()).getType() == selectionItem) {
                e.setCancelled(true);
                SelectionMode selectionMode = RegionEditPlugin.selectionMode.getOrDefault(e.getPlayer().getUniqueId(), SelectionMode.CUBOID);
                if (selectionMode == SelectionMode.CUBOID) {
                    CuboidRegion cuboidRegion = (CuboidRegion) regionSelection.getOrDefault(e.getPlayer().getUniqueId(), new CuboidRegion(e.getClickedBlock().getLocation(), e.getClickedBlock().getLocation()));
                    if (!Objects.requireNonNull(cuboidRegion.getLocation()).getWorld().equals(e.getClickedBlock().getWorld()))
                        cuboidRegion = new CuboidRegion(e.getClickedBlock().getLocation(), e.getClickedBlock().getLocation());
                    CuboidRegion reg = new CuboidRegion(cuboidRegion.getLocation(), e.getClickedBlock().getLocation());
                    selectRegion(reg, e.getPlayer());
                }
            }
        }
        if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
            UserSession session = getUserSession(e.getPlayer());
            if (e.getClickedBlock() != null) {
                if (Reflection.getItemInHand(e.getPlayer()).getType().name().endsWith("PICKAXE") && e.getPlayer().hasPermission("regionedit.superpickaxe")) {
                    if (session.getSuperPickaxeMode() == SuperPickaxeMode.AREA) {
                        final Material type = e.getClickedBlock().getType();
                        final byte data = Reflection.getData(e.getClickedBlock());
                        World world = e.getClickedBlock().getWorld();
                        RegionEdit.getNearbyBlocksAsync(e.getClickedBlock().getLocation(), getUserSession(e.getPlayer()).getSuperPickaxeRadius(), (blocks, e1) -> {
                            blocks = blocks.filter(block -> block.getType() == type && Reflection.getData(block) == data);
                            blocks.forEach(block -> BlockUtil.setBlockSendBlockChange(world, block.getX(), block.getY(), block.getZ(), Material.AIR, (byte) 0, Reflection.createBlockData(block.getLocation(), Material.AIR)));
                            //Blocks.sendBlockChanges(blocks, type, data);
                        });
                    } else if (session.getSuperPickaxeMode() == SuperPickaxeMode.DESTROYER) {
                        //final Material type = e.getClickedBlock().getType();
                        //final byte data = Reflection.getData(e.getClickedBlock());
                        World world = e.getClickedBlock().getWorld();
                        RegionEdit.getNearbyBlocksAsync(e.getClickedBlock().getLocation(), getUserSession(e.getPlayer()).getSuperPickaxeRadius(), (blocks, e1) -> {
                            blocks.forEach(block -> BlockUtil.setBlockSendBlockChange(world, block.getX(), block.getY(), block.getZ(), Material.AIR, (byte) 0, Reflection.createBlockData(block.getLocation(), Material.AIR)));
                            //Blocks.sendBlockChanges(blocks, type, data);
                        });
                    } else if (session.getSuperPickaxeMode() == SuperPickaxeMode.SINGLE) {
                        e.getClickedBlock().breakNaturally();
                    } else if (session.getSuperPickaxeMode() == SuperPickaxeMode.AREA_DROP) {
                        final Material type = e.getClickedBlock().getType();
                        final byte data = Reflection.getData(e.getClickedBlock());
                        World world = e.getClickedBlock().getWorld();
                        RegionEdit.getNearbyBlocksAsync(e.getClickedBlock().getLocation(), getUserSession(e.getPlayer()).getSuperPickaxeRadius(), (blocks, e1) -> {
                            blocks = blocks.filter(block -> block.getType() == type && Reflection.getData(block) == data);
                            blocks.forEach(block -> {
                                if (type == Material.AIR) return;
                                Bukkit.getScheduler().runTask(RegionEdit.getInstance(), () -> {
                                    Item item = world.spawn(block.getLocation(), Item.class);
                                    item.setItemStack(data == 0 ? new ItemStack(type) : new ItemStack(type, 1, data));
                                    item.setPickupDelay(0);
                                });
                                BlockUtil.setBlock(world, block.getX(), block.getY(), block.getZ(), Material.AIR, (byte) 0, Reflection.createBlockData(block.getLocation(), Material.AIR));
                            });
                            BlockUtil.sendBlockChanges(blocks, type, data);
                        });
                    }
                }
            }
            if (e.getPlayer().hasPermission("regionedit.navigation") && Reflection.getItemInHand(e.getPlayer()).getType() == navigationItem) {
                Block block = e.getPlayer().getTargetBlock((Set<Material>) null, 500);
                e.setCancelled(true);
                if (block == null || block.getType() == Material.AIR) {
                    e.getPlayer().sendMessage(ChatColor.RED + "No blocks in sight! (or too far)");
                    return;
                }
                int i = 0;
                while (i < 256 && block.getLocation().add(0, i+1, 0).getBlock().getType() != Material.AIR) i++;
                Location location = block.getLocation().clone().add(0.5, i+1, 0.5);
                location.setYaw(e.getPlayer().getLocation().getYaw());
                location.setPitch(e.getPlayer().getLocation().getPitch());
                e.getPlayer().teleport(location);
            }
        }
    }

    private void selectRegion(@NotNull CuboidRegion reg, @NotNull Player player) {
        regionSelection.add(player.getUniqueId(), reg);
        getUserSession(player).sendCUIEvent();
        showCurrentRegion(player);
    }

    public static void showCurrentRegion(Player player) {
        CuboidRegion reg = (CuboidRegion) regionSelection.get(player.getUniqueId());
        assert reg.getLocation() != null;
        assert reg.getLocation2() != null;
        player.sendMessage(ChatColor.GREEN + "Selected region "
                + ChatColor.YELLOW + "(" + loc2Str(reg.getLocation()) + " -> " + loc2Str(reg.getLocation2()) + ") "
                + ChatColor.LIGHT_PURPLE + "(" + reg.size() + " blocks)");
    }

    public static String loc2Str(Location location) {
        if (location == null) return "null";
        return String.format(ChatColor.LIGHT_PURPLE + "%d, %d, %d" + ChatColor.YELLOW, location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static final AtomicInteger taskId = new AtomicInteger();

    // task id : cancelled state
    public static final Collection<Integer, OperationStatus> tasks = new Collection<>();

    public static final Collection<UUID, CollectionList<Integer>> playerTasks = new Collection<>();

    @Override
    public void setBlocks(@Nullable Player player, @NotNull ICollectionList<Block> blocks, final Material material, final byte data) {
        setBlocks0(player, blocks, material, data);
    }

    public static void setBlocks0(@Nullable Player player, @NotNull ICollectionList<Block> blocks, final Material material, final byte data) {
        ICollectionList<xyz.acrylicstyle.region.api.block.Block> blocks2 = blocks.map(RegionBlock::new);
        if (player != null && !playerTasks.containsKey(player.getUniqueId())) playerTasks.add(player.getUniqueId(), new CollectionList<>());
        Plugin plugin = RegionEdit.getInstance();
        AtomicInteger i0 = new AtomicInteger();
        AtomicInteger i = new AtomicInteger();
        final int taskId = RegionEditPlugin.taskId.getAndIncrement();
        if (player != null) playerTasks.get(player.getUniqueId()).add(taskId);
        tasks.add(taskId, OperationStatus.RUNNING);
        final boolean fastMode = player == null || sessions.get(player.getUniqueId()).isFastMode();
        if (player != null) player.sendMessage("" + ChatColor.RED + blocks.size() + ChatColor.GREEN + " blocks will be affected. " + ChatColor.LIGHT_PURPLE + " (Task ID: " + taskId + ")");
        int size = blocks.size();
        int in = Math.max((int) (size / 1000D), 1);
        long start = System.currentTimeMillis();
        if (fastMode) {
            split(blocks, 1000).forEach(list -> pool.execute(() -> list.map(RegionBlock::wrap).forEach(block -> {
                try {
                    int x = block.getLocation().getBlockX();
                    int y = block.getLocation().getBlockY();
                    int z = block.getLocation().getBlockZ();
                    World world = block.getLocation().getWorld();
                    BlockUtil.setBlock(world, x, y, z, material, data, Reflection.createBlockData(block.getLocation(), material));
                } finally {
                    showPercentage(i0, size, in, player, taskId);
                }
            })));
        } else {
            blocks.map(RegionBlock::wrap).forEach(block -> new Thread(() -> new BukkitRunnable() {
                @Override
                public void run() {
                    if (tasks.get(taskId) == OperationStatus.CANCELLED) return;
                    block.setTypeAndData(material, data, Reflection.createBlockData(block.getLocation(), material), true);
                    showPercentage(i0, size, in, player, taskId);
                }
            }.runTaskLater(plugin, i0.get() % RegionEditPlugin.blocksPerTick == 0 ? i.getAndIncrement() : i.get())).start());
        }
        CollectionSet<org.bukkit.Chunk> entries = new CollectionSet<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (player != null) {
                            if (blocks2.size() <= 30000) {
                                historyManager.addEntry(player.getUniqueId(), blocks2);
                            } else {
                                player.sendMessage(ChatColor.YELLOW + "History was not saved (large edit).");
                            }
                        }
                        if (fastMode) {
                            while (true) {
                                if (i0.get() >= blocks.size()) {
                                    long end = System.currentTimeMillis();
                                    completeOperation(blocks.size(), taskId, start, end, player);
                                    Log.debug("Relighting " + entries.size() + " chunks");
                                    entries.forEach(e -> {
                                        Chunk chunk = Chunk.getInstance(e);
                                        chunk.initLighting();
                                        if (player != null) Reflection.sendChunk(player, chunk);
                                        Reflection.markDirty(e);
                                    });
                                    break;
                                }
                            }
                        }
                    }
                }.runTaskLaterAsynchronously(RegionEdit.getInstance(), 5);
                Log.debug("Updating " + blocks.size() + " blocks");
                AtomicInteger i1 = new AtomicInteger();
                blocks.forEach(b -> {
                    entries.add(b.getChunk());
                    for (Player p : Bukkit.getOnlinePlayers())
                        Reflection.sendBlockChange(p, b.getLocation(), material, data, Reflection.createBlockData(b.getLocation(), material));
                    showAndIncrementPercentage(i1, size, in, player, "Sending block changes", "Blocks");
                    // if (!fastMode) Reflection.notify(b.getWorld(), b, Reflection.newRawBlockPosition(b.getLocation().getBlockX(), b.getLocation().getBlockY(), b.getLocation().getBlockZ()));
                });
            }
        }.runTaskLaterAsynchronously(plugin, i.getAndIncrement());
    }

    private static void showPercentage(AtomicInteger i0, int size, int in, Player player, int taskId) {
        double p = Math.round(i0.incrementAndGet() / (double) size * 1000) / 10D;
        if (i0.get() % in == 0 || i0.get() == size) {
            TomeitoAPI.sendActionbar(player, ChatColor.GREEN + "ID " + ChatColor.YELLOW + taskId + ChatColor.GOLD + " | "
                    + ChatColor.GREEN + "Blocks: " + ChatColor.YELLOW + i0.get() + ChatColor.LIGHT_PURPLE + " / " + ChatColor.YELLOW + size
                    + ChatColor.GOLD + " | " + ChatColor.YELLOW + p + "%");
        }
    }

    private static void showAndIncrementPercentage(AtomicInteger i0, int size, int in, Player player, String action, String ps) {
        if (player == null) return;
        showPercentage(i0.incrementAndGet(), size, in, player, action, ps);
    }

    private static void showPercentage(int i0, int size, int in, Player player, String action, String ps) {
        if (player == null) return;
        double p = Math.round(i0 / (double) size * 1000) / 10D;
        if (i0 % in == 0 || i0 == size) {
            TomeitoAPI.sendActionbar(player, ChatColor.LIGHT_PURPLE + action + " | "
                    + ChatColor.GREEN + ps + ": " + ChatColor.YELLOW + i0 + ChatColor.LIGHT_PURPLE + " / " + ChatColor.YELLOW + size
                    + ChatColor.GOLD + " | " + ChatColor.YELLOW + p + "%");
        }
    }

    private static void completeOperation(int size, int taskId, long start, long end, Player player) {
        if (player == null) return;
        if (tasks.get(taskId) == OperationStatus.CANCELLED) return;
        tasks.add(taskId, OperationStatus.FINISHED);
        double seconds = Math.round((end - start) / 1000D * 100) / 100D;
        int bpt = (int) (size / ((float) (end - start) / 50D)); // 50ms
        player.sendMessage(ChatColor.GREEN + "Operation completed. " + ChatColor.LIGHT_PURPLE + "(" + bpt + " blocks per ticks, took " + seconds + " seconds)");
    }

    public static void setBlocks(Player player, Collection<BlockPos, BlockState> blocks) {
        setBlocks(player, blocks, false);
    }

    public static void setBlocks(Player player, Collection<BlockPos, BlockState> blocks, boolean history) {
        AsyncCatcher.setEnabled(false);
        final int taskId = RegionEditPlugin.taskId.getAndIncrement();
        final Plugin plugin = RegionEdit.getInstance();
        final AtomicInteger i0 = new AtomicInteger();
        final AtomicInteger i = new AtomicInteger();
        final boolean fastMode = sessions.get(player.getUniqueId()).isFastMode();
        final int size = blocks.size();
        final int in = Math.max((int) (size / 1000D), 1);
        final World world = blocks.size() == 0 ? null : Objects.requireNonNull(blocks.firstKey()).getWorld();
        if (world == null) return;
        if (history) {
            pool.execute(() -> {
                Log.as("RegionEdit").info("[" + taskId + "] Adding into the history in background... (" + player.getName() + ")");
                if (blocks.size() <= 30000) {
                    historyManager.addEntry(player.getUniqueId(), blocks);
                    Log.as("RegionEdit").info("[" + taskId + "] Added into the history (" + player.getName() + ")");
                } else {
                    player.sendMessage(ChatColor.YELLOW + "History was not saved (large edit).");
                    Log.as("RegionEdit").info("[" + taskId + "] History was not saved (large edit) (" + player.getName() + ")");
                }
            });
        }
        if (!playerTasks.containsKey(player.getUniqueId())) playerTasks.add(player.getUniqueId(), new CollectionList<>());
        playerTasks.get(player.getUniqueId()).add(taskId);
        tasks.add(taskId, OperationStatus.RUNNING);
        player.sendMessage("" + ChatColor.RED + blocks.size() + ChatColor.GREEN + " blocks will be affected. " + ChatColor.LIGHT_PURPLE + " (Task ID: " + taskId + ")");
        long start = System.currentTimeMillis();
        blocks.forEach((loc, block) -> {
            if (!fastMode) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (tasks.get(taskId) == OperationStatus.CANCELLED) return;
                        block.update(world);
                        showPercentage(i0, size, in, player, taskId);
                    }
                }.runTaskLater(plugin, i0.get() % RegionEditPlugin.blocksPerTick == 0 ? i.getAndIncrement() : i.get());
            } else {
                split(blocks, 1000).forEach(list -> pool.execute(() -> {
                    if (tasks.get(taskId) == OperationStatus.CANCELLED) return;
                    block.updateFast(world);
                    showPercentage(i0, size, in, player, taskId);
                }));
            }
        });
        long end = System.currentTimeMillis();
        new BukkitRunnable() {
            @Override
            public void run() {
                new BukkitRunnable() {
                    @SuppressWarnings("deprecation")
                    @Override
                    public void run() {
                        while (true) {
                            if (i0.get() >= blocks.size()) {
                                Log.debug("Unloading chunks");
                                RegionEdit.unloadChunks(blocks);
                                Log.debug("Updating " + blocks.size() + " blocks");
                                AtomicInteger i1 = new AtomicInteger();
                                blocks.valuesList().forEach(b -> {
                                    i1.incrementAndGet();
                                    for (Player p : Bukkit.getOnlinePlayers()) {
                                        Reflection.sendBlockChange(p, b.getBlockPos(world).getLocation(), b.getType(), b.getData(), b.getPropertyMap() == null ? null : b.getPropertyMap().getIBlockData(new MaterialData(b.getType(), b.getData())));
                                        showPercentage(i1.get(), size, in, player, "Sending block changes", "Blocks");
                                    }
                                    // Reflection.notify(b.getLocation().getWorld(), b.getBukkitBlock(), new BlockPosition(b.getLocation().getBlockX(), b.getLocation().getBlockY(), b.getLocation().getBlockZ()).getHandle());
                                });
                                CollectionSet<org.bukkit.Chunk> chunks = RegionEdit.getChunks(blocks);
                                Log.debug("Relighting " + chunks.size() + " chunks");
                                AtomicInteger ch = new AtomicInteger();
                                chunks.forEach(chunk -> {
                                    Chunk c = Chunk.getInstance(chunk);
                                    c.initLighting();
                                    Reflection.sendChunk(player, c);
                                    Reflection.markDirty(chunk);
                                    showAndIncrementPercentage(ch, chunks.size(), 1, player, "Relighting chunks", "Chunks");
                                });
                                AsyncCatcher.setEnabled(true);
                                break;
                            }
                        }
                    }
                }.runTaskLaterAsynchronously(RegionEdit.getInstance(), 10);
                completeOperation(blocks.size(), taskId, start, end, player);
            }
        }.runTaskLater(plugin, i.getAndIncrement());
    }
}
