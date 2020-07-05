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
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Collection;
import util.CollectionList;
import util.ICollectionList;
import util.javascript.JavaScript;
import xyz.acrylicstyle.minecraft.BlockPosition;
import xyz.acrylicstyle.region.api.RegionEdit;
import xyz.acrylicstyle.region.api.exception.RegionEditException;
import xyz.acrylicstyle.region.api.operation.OperationStatus;
import xyz.acrylicstyle.region.api.player.SuperPickaxeMode;
import xyz.acrylicstyle.region.api.player.UserSession;
import xyz.acrylicstyle.region.api.region.CuboidRegion;
import xyz.acrylicstyle.region.api.region.RegionSelection;
import xyz.acrylicstyle.region.api.selection.SelectionMode;
import xyz.acrylicstyle.region.internal.block.Blocks;
import xyz.acrylicstyle.region.internal.block.RegionBlock;
import xyz.acrylicstyle.region.internal.command.CommandDescription;
import xyz.acrylicstyle.region.internal.command.CommandDescriptionManager;
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
import xyz.acrylicstyle.region.internal.commands.Pos1Command;
import xyz.acrylicstyle.region.internal.commands.Pos2Command;
import xyz.acrylicstyle.region.internal.commands.RedoCommand;
import xyz.acrylicstyle.region.internal.commands.RegionEditCommand;
import xyz.acrylicstyle.region.internal.commands.ReplaceCommand;
import xyz.acrylicstyle.region.internal.commands.SelectionCommand;
import xyz.acrylicstyle.region.internal.commands.SetCommand;
import xyz.acrylicstyle.region.internal.commands.SuperPickaxeCommand;
import xyz.acrylicstyle.region.internal.commands.UndoCommand;
import xyz.acrylicstyle.region.internal.commands.UnstuckCommand;
import xyz.acrylicstyle.region.internal.commands.WandCommand;
import xyz.acrylicstyle.region.internal.listener.CUIChannelListener;
import xyz.acrylicstyle.region.internal.manager.HistoryManagerImpl;
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

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Internal usage only
 */
public class RegionEditPlugin extends JavaPlugin implements RegionEdit, Listener {
    public static final String CUI = "worldedit:cui";
    public static final String CUI_LEGACY = "WECUI";

    private Material selectionItem = null;
    private Material navigationItem = null;

    private static final HistoryManagerImpl historyManager = new HistoryManagerImpl();
    public static final Collection<UUID, SelectionMode> selectionMode = new Collection<>();
    public static final Collection<UUID, RegionSelection> regionSelection = new Collection<>();
    public static final CommandDescriptionManager commandDescriptionManager = new CommandDescriptionManager();

    public static final Collection<UUID, UserSession> sessions = new Collection<>();

    public static int blocksPerTick = 4096;

    @Override
    public void onEnable() {
        if (Compatibility.getBukkitVersion() == BukkitVersion.UNKNOWN) {
            Log.as("RegionEdit").warning("You are using an unknown/unsupported bukkit version.");
            Log.as("RegionEdit").warning("Disabling plugin as this plugin will very likely break.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Bukkit.getMessenger().registerIncomingPluginChannel(this, CUI, new CUIChannelListener(this));
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, CUI);
        Bukkit.getMessenger().registerIncomingPluginChannel(this, CUI_LEGACY, new CUIChannelListener(this));
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, CUI_LEGACY);
        Log.info("Registering events");
        Bukkit.getServicesManager().register(RegionEdit.class, this, this, ServicePriority.Normal);
        Bukkit.getPluginManager().registerEvents(this, this);
        Log.info("Registering commands");
        TomeitoAPI.registerCommand("regionedit", new RegionEditCommand());
        TomeitoAPI.registerCommand("/wand", new WandCommand());
        TomeitoAPI.registerCommand("/distr", new DistributionCommand());
        TomeitoAPI.registerCommand("/help", new HelpCommand());
        TomeitoAPI.registerCommand("sel", new SelectionCommand());
        TomeitoAPI.registerCommand("/set", new SetCommand());
        TomeitoAPI.registerCommand("/limit", new LimitCommand());
        TomeitoAPI.registerCommand("/pos1", new Pos1Command());
        TomeitoAPI.registerCommand("/pos2", new Pos2Command());
        TomeitoAPI.registerCommand("/replace", new ReplaceCommand());
        TomeitoAPI.registerCommand("/hpos1", new HPos1Command());
        TomeitoAPI.registerCommand("/hpos2", new HPos2Command());
        TomeitoAPI.registerCommand("/cut", new CutCommand());
        TomeitoAPI.registerCommand("/undo", new UndoCommand());
        TomeitoAPI.registerCommand("/redo", new RedoCommand());
        TomeitoAPI.registerCommand("/cancel", new CancelCommand());
        TomeitoAPI.registerCommand("/drain", new DrainCommand());
        TomeitoAPI.registerCommand("/expand", new ExpandCommand());
        TomeitoAPI.registerCommand("/fast", new FastCommand());
        TomeitoAPI.registerCommand("/unstuck", new UnstuckCommand());
        TomeitoAPI.registerCommand("/chunk", new ChunkCommand());
        TomeitoAPI.registerCommand("/drawsel", new DrawSelCommand());
        TomeitoAPI.registerCommand("/sp", new SuperPickaxeCommand());
        Log.info("Registering tab completers");
        Bukkit.getPluginCommand("regionedit").setTabCompleter(new RegionEditTabCompleter());
        Bukkit.getPluginCommand("/set").setTabCompleter(new BlocksTabCompleter());
        Bukkit.getPluginCommand("/replace").setTabCompleter(new ReplaceBlocksTabCompleter());
        Bukkit.getPluginCommand("/drain").setTabCompleter(new DrainTabCompleter());
        Log.info("Registering command help");
        commandDescriptionManager.add("//help", new CommandDescription("//help [page]", "regionedit.help", "Shows all RegionEdit commands."));
        commandDescriptionManager.add("/re", new CommandDescription("/re <help/version/reload/commands/compatibility>",
                Arrays.asList("regionedit.compatibility", "regionedit.reload", "regionedit.help"),
                "Displays information about RegionEdit."));
        commandDescriptionManager.add("/;", new CommandDescription("//sel [cuboid]", "", "Clears selection or switches selection mode."));
        commandDescriptionManager.add("//sel", new CommandDescription("//sel [cuboid]", "", "Clears selection or switches selection mode."));
        commandDescriptionManager.add("//limit", new CommandDescription("//limit [number]", "regionedit.limit", "Limits blocks per ticks"));
        commandDescriptionManager.add("//pos1", new CommandDescription("//pos1", "regionedit.selection", "Set position 1 at player's location."));
        commandDescriptionManager.add("//pos2", new CommandDescription("//pos2", "regionedit.selection", "Set position 2 at player's location."));
        commandDescriptionManager.add("//hpos1", new CommandDescription("//hpos1", "regionedit.selection", "Set position 1 at block player's is looking at."));
        commandDescriptionManager.add("//hpos2", new CommandDescription("//hpos2", "regionedit.selection", "Set position 2 at block player's is looking at."));
        commandDescriptionManager.add("//cut", new CommandDescription("//cut", "regionedit.cut", "Removes blocks at specified region."));
        commandDescriptionManager.add("//replace", new CommandDescription("//replace [before] [after]", "regionedit.replace", "Replace blocks at specified region."));
        commandDescriptionManager.add("//set", new CommandDescription("//set [block]", "regionedit.set", "Places blocks at specified region."));
        commandDescriptionManager.add("//undo", new CommandDescription("//undo", "regionedit.undo", "Rollbacks action."));
        commandDescriptionManager.add("//redo", new CommandDescription("//redo", "regionedit.redo", "Rollbacks undo action."));
        commandDescriptionManager.add("//drain", new CommandDescription("//drain [radius] [lava]", "regionedit.drain", "Drains water near you."));
        commandDescriptionManager.add("//cancel", new CommandDescription("//cancel [task id/all]",
                Arrays.asList("regionedit.cancel", "regionedit.cancel.a -> self", "regionedit.cancel.b -> others", "regionedit.cancel.c -> all"),
                "Cancels current operation."));
        commandDescriptionManager.add("//expand", new CommandDescription("//expand <<<number> <up/down/east/south/west/north>>/<vert>>", "regionedit.expand", "Expands selection area by <number>."));
        commandDescriptionManager.add("//fast", new CommandDescription("//fast", "regionedit.fast", "Toggles fast mode.", "Fast mode disables some physics on operation."));
        commandDescriptionManager.add("//unstuck", new CommandDescription("//unstuck", "regionedit.unstuck", "Get out of stuck."));
        commandDescriptionManager.add("//chunk", new CommandDescription("//chunk", "regionedit.selection", "Selects an entire chunk."));
        commandDescriptionManager.add("//wand", new CommandDescription("//wand", "regionedit.wand", "Gives player a wand (item) to get started with RegionEdit."));
        commandDescriptionManager.add("//distr", new CommandDescription("//distr [--exclude=blocks,separated,by,comma]", "regionedit.distr", "Shows block distribution."));
        commandDescriptionManager.add("//", new CommandDescription("/sp <area <radius>/single/drop <radius>/off>", "regionedit.superpickaxe", "Toggles super pickaxe mode."));
        commandDescriptionManager.add("//drawsel", new CommandDescription("//drawsel", "regionedit.drawsel", "Toggles draws selection mode"));
        selectionItem = Material.getMaterial(this.getConfig().getString("selection_item", Compatibility.getGoldenAxe().name()));
        navigationItem = Material.getMaterial(this.getConfig().getString("navigation_item", "COMPASS"));
        Log.info("Detected server version: " + Compatibility.getBukkitVersion().getName());
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
                            blocks.forEach(block -> Blocks.setBlockSendBlockChange(world, block.getX(), block.getY(), block.getZ(), Material.AIR, (byte) 0, Reflection.createBlockData(block.getLocation(), Material.AIR)));
                            Blocks.sendBlockChanges(blocks, type, data);
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
                                Bukkit.getScheduler().runTask(RegionEdit.getInstance(), () -> {
                                    Item item = world.spawn(block.getLocation(), Item.class);
                                    item.setItemStack(data == 0 ? new ItemStack(type) : new ItemStack(type, 1, data));
                                    item.setPickupDelay(0);
                                });
                                Blocks.setBlock(world, block.getX(), block.getY(), block.getZ(), Material.AIR, (byte) 0, Reflection.createBlockData(block.getLocation(), Material.AIR));
                            });
                            Blocks.sendBlockChanges(blocks, type, data);
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
    public @NotNull Material getWandItem() {
        return selectionItem;
    }

    @Override
    public @NotNull Material getNavigationItem() {
        return navigationItem;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public @Nullable Map.Entry<Material, Byte> resolveMaterial(@NotNull String id) {
        if (id.matches("^\\d+|\\d+:\\d+$")) {
            String[] arr = id.split(":");
            int data;
            if (arr.length != 1) {
                data = Integer.parseInt(arr[1]);
            } else {
                data = 0;
            }
            return new AbstractMap.SimpleImmutableEntry<>(Blocks.getMaterialById(Integer.parseInt(id)), (byte) data);
        } else {
            CollectionList<String> materials = ICollectionList.asList(Material.values()).filter(Material::isBlock).map(Enum::name).map((Function<String, String>) String::toLowerCase);
            if (!materials.contains(id.split(":")[0].toLowerCase())) {
                return null;
            }
            int data = JavaScript.parseInt((id + ":0").split(":")[1], 10);
            return new AbstractMap.SimpleImmutableEntry<>(Material.getMaterial(Objects.requireNonNull(materials.filter(s -> s.equalsIgnoreCase((id + ":0").split(":")[0])).first()).toUpperCase()), (byte) data);
        }
    }

    @Override
    @NotNull
    public UserSession getUserSession(@NotNull final UUID uuid) {
        if (!sessions.containsKey(uuid)) sessions.add(uuid, new UserSessionImpl(this, uuid));
        return sessions.get(uuid);
    }

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
    public HistoryManagerImpl getHistoryManager() { return historyManager; }

    // task id : cancelled state
    public static final AtomicInteger taskId = new AtomicInteger();
    public static final Collection<Integer, OperationStatus> tasks = new Collection<>();

    public static final Collection<UUID, CollectionList<Integer>> playerTasks = new Collection<>();

    public static void setBlocks(Player player, @NotNull CollectionList<Block> blocks, final Material material, final byte data) {
        double start = System.currentTimeMillis();
        if (Compatibility.checkChunkSection()) {
            Log.info("Using ChunkSection#setType");
        } else {
            Log.info("Using Chunk#setType");
        }
        CollectionList<xyz.acrylicstyle.region.api.block.Block> blocks2 = blocks.map(RegionBlock::new);
        if (!playerTasks.containsKey(player.getUniqueId())) playerTasks.add(player.getUniqueId(), new CollectionList<>());
        Plugin plugin = RegionEdit.getInstance();
        AtomicInteger i0 = new AtomicInteger();
        AtomicInteger i = new AtomicInteger();
        final int taskId = RegionEditPlugin.taskId.getAndIncrement();
        playerTasks.get(player.getUniqueId()).add(taskId);
        tasks.add(taskId, OperationStatus.RUNNING);
        final boolean fastMode = sessions.get(player.getUniqueId()).isFastMode();
        player.sendMessage("" + ChatColor.RED + blocks.size() + ChatColor.GREEN + " blocks affected. " + ChatColor.LIGHT_PURPLE + " (Task ID: " + taskId + ")");
        blocks.map(RegionBlock::wrap).forEach(block -> {
            if (!fastMode) {
                new Thread(() -> new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (tasks.get(taskId) == OperationStatus.CANCELLED) return;
                        block.setTypeAndData(material, data, Reflection.createBlockData(block.getLocation(), material), true);
                    }
                }.runTaskLater(plugin, i0.get() % RegionEditPlugin.blocksPerTick == 0 ? i.getAndIncrement() : i.get())).start();
            } else {
                int x = block.getLocation().getBlockX();
                int y = block.getLocation().getBlockY();
                int z = block.getLocation().getBlockZ();
                World world = block.getLocation().getWorld();
                Blocks.setBlock(world, x, y, z, material, data, Reflection.createBlockData(block.getLocation(), material));
            }
            i0.incrementAndGet();
        });
        CollectionList<Map.Entry<Integer, Integer>> entries = new CollectionList<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        historyManager.addEntry(player.getUniqueId(), blocks2);
                        if (fastMode) {
                            while (true) {
                                if (i0.get() >= blocks.size()) {
                                    Log.debug("Relighting " + entries.unique().size() + " chunks");
                                    entries.unique().forEach(e -> {
                                        Chunk chunk = Chunk.wrap(Objects.requireNonNull(blocks.first()).getWorld().getChunkAt(e.getKey(), e.getValue()));
                                        chunk.initLighting();
                                        Reflection.sendChunk(player, chunk);
                                    });
                                    break;
                                }
                            }
                        }
                    }
                }.runTaskLaterAsynchronously(RegionEdit.getInstance(), 10);
                completeOperation(blocks.size(), taskId, start, player);
                Log.debug("Updating " + blocks.size() + " blocks");
                blocks.forEach(b -> entries.add(new AbstractMap.SimpleEntry<>(b.getChunk().getX(), b.getChunk().getZ())));
                blocks.forEach(b -> {
                    for (Player p : Bukkit.getOnlinePlayers())
                        Reflection.sendBlockChange(p, b.getLocation(), material, data, Reflection.createBlockData(b.getLocation(), material));
                    Reflection.notify(b.getWorld(), b, Reflection.newRawBlockPosition(b.getLocation().getBlockX(), b.getLocation().getBlockY(), b.getLocation().getBlockZ()));
                    Reflection.markDirty(b.getChunk());
                });
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
        final boolean fastMode = sessions.get(player.getUniqueId()).isFastMode();
        player.sendMessage("" + ChatColor.RED + blocks.size() + ChatColor.GREEN + " blocks affected. " + ChatColor.LIGHT_PURPLE + " (Task ID: " + taskId + ")");
        blocks.forEach((loc, block) -> {
            if (!fastMode) {
                new Thread(() -> new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (tasks.get(taskId) == OperationStatus.CANCELLED) return;
                        block.setTypeAndData(block.getType(), block.getData(), Reflection.createBlockData(block.getLocation(), block.getType()), true);
                    }
                }.runTaskLater(plugin, i0.get() % RegionEditPlugin.blocksPerTick == 0 ? i.getAndIncrement() : i.get())).start();
                i0.incrementAndGet();
            } else {
                int x = block.getLocation().getBlockX();
                int y = block.getLocation().getBlockY();
                int z = block.getLocation().getBlockZ();
                World world = block.getLocation().getWorld();
                new Thread(() -> {
                    Blocks.setBlock(world, x, y, z, block.getType(), block.getData(), Reflection.createBlockData(block.getLocation(), block.getType()));
                    i0.incrementAndGet();
                }).start();
            }
        });
        new BukkitRunnable() {
            @Override
            public void run() {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        while (true) {
                            if (i0.get() >= blocks.size()) {
                                Log.debug("Updating " + blocks.size() + " blocks");
                                blocks.valuesList().forEach(b -> {
                                    for (Player p : Bukkit.getOnlinePlayers()) Reflection.sendBlockChange(p, b.getLocation(), b.getType(), b.getData(), Reflection.getBlockData(b.getBukkitBlock()));
                                    Reflection.notify(b.getLocation().getWorld(), b.getBukkitBlock(), new BlockPosition(b.getLocation().getBlockX(), b.getLocation().getBlockY(), b.getLocation().getBlockZ()).getHandle());
                                    Reflection.markDirty(b.getLocation().getChunk());
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            b.getLocation().getChunk().unload();
                                            b.getLocation().getChunk().load();
                                        }
                                    }.runTask(plugin);
                                });
                                break;
                            }
                        }
                    }
                }.runTaskLaterAsynchronously(RegionEdit.getInstance(), 10);
                completeOperation(blocks.size(), taskId, start, player);
            }
        }.runTaskLater(plugin, i.getAndIncrement());
    }
}
