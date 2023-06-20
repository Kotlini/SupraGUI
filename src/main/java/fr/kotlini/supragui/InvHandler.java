package fr.kotlini.supragui;

import fr.kotlini.supragui.bases.GUI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class InvHandler {

    private static final AtomicBoolean REGISTERED = new AtomicBoolean(false);

    private static InvHandler instance;

    private final Map<UUID, GUI> guis;

    public InvHandler() {
        guis = new HashMap<>();
    }

    public static void put(UUID uuid, GUI inventory) {
        get().getGuis().put(uuid, inventory);
    }

    public static void remove(UUID uuid) {
        get().getGuis().remove(uuid);
    }

    public static Optional<GUI> findMenu(UUID uuid) {
        return Optional.ofNullable(get().getGuis().getOrDefault(uuid, null));
    }

    public static void register(Plugin plugin) {
        Objects.requireNonNull(plugin, "plugin");

        if (REGISTERED.getAndSet(true)) {
            throw new IllegalStateException("SupraGUI is already registered");
        }

        Bukkit.getPluginManager().registerEvents(new InventoryListener(plugin), plugin);
    }

    public static void closeAll() {
        get().getGuis().forEach((uuid, gui) -> gui.close());
    }

    public Map<UUID, GUI> getGuis() {
        return guis;
    }

    public static InvHandler get() {
        if (InvHandler.instance == null) {
            InvHandler.instance = new InvHandler();
        }

        return InvHandler.instance;
    }

    public static class InventoryListener implements Listener {

        private final Plugin plugin;

        public InventoryListener(Plugin plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            if (event.getCurrentItem() == null) return;

            InvHandler.findMenu(event.getWhoClicked().getUniqueId()).ifPresent(value -> {
                if (value.getItemHandlers().get((value.getSize() * value.getIndex() - value.getSize()) + event.getRawSlot()) != null) {
                    final boolean wasCancelled = event.isCancelled();
                    event.setCancelled(true);

                    value.handleClick(event);

                    // This prevents un-canceling the event if another plugin canceled it before
                    if (!wasCancelled && !event.isCancelled()) {
                        event.setCancelled(false);
                    }
                }
            });
        }

        @EventHandler
        public void onInventoryOpen(InventoryOpenEvent event) {
            InvHandler.findMenu(event.getPlayer().getUniqueId()).ifPresent(value -> {
                value.handleOpen(event);
            });
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent event) {
            InvHandler.findMenu(event.getPlayer().getUniqueId()).ifPresent(value -> {
                if (value.handleClose(event)) {
                    Bukkit.getScheduler().runTask(plugin, () -> value.open(false));
                } else {
                    value.unRegister();
                }
            });
        }

        @EventHandler
        public void onPluginDisable(PluginDisableEvent e) {
            if (e.getPlugin() == this.plugin) {
                closeAll();

                REGISTERED.set(false);
            }
        }
    }
}